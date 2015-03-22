# Introduction #

This page describes the high level architecture of the application from the developer's perspective.

# Application Architecture #

The application is mainly divided into two areas: the domain and logic code, and the user interface code. The domain contains the classes that hold the game information, and the basic application logic, such as importing files. The user interface holds all code relevant to rendering the ui.

## Domain Model ##

All information about the game is stored in the **Game** class (org.joverseer.game.Game). The game contains **metadata** information and **turn** information.

The **metadata** is the background information, such as the map information (hexes, terrain, etc), nations (nation names, SNAs, etc), order information (order numbers, names, and validation info) and the starting information (starting characters, pop centers and armies).

The **turn** information contains the actual game information for each turn of the game, such as characters, pop centers, armies, rumors, artifacts, etc.

### Metadata ###

To do

### Turn Information ###

The turn information is stored in the **Turn** class (org.joverseer.game.Turn). The turn stores the turn number, the turn date, the season, and a hashtable of containers, which hold the actual domain objects.

The **container** is an integral part of the application as it provides facilities for storing a list of objects, and searching and updating them efficiently. The container (org.joverseer.support.Container) basically stores an `ArrayList` of objects, and provides caching and the ability to find objects by one of their properties (e.g. `findAllByProperty("hexNo", 3120)` will returns all items stored in the container whose hexNo property is equal to 3120).

The **`TurnElementsEnum`** enumerates all objects that are storable in the turn's containers (Characters, Population Centers, etc). All items are represented by a domain object (e.g. org.joverseer.domain.Character).

The **`GameHolder`** class provides static access to the currently loaded game. So, to get all characters located at 3120 for the current turn of the game, one can use:
`GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("hexNo", 3120)`

### Interfaces ###

There are some standard interfaces that all domain objects implement. These are:
  * **`IHasMapLocation`** --> the domain object is associated with a location (hex) on the map. Characters, Pop Centers, Armies, Combats, are some domain objects that implement this.
  * **`IHasTurnNumber`** --> the domain object is associated with a specific turn.
  * **`IBelongsToNation`** --> the domain object is associated with a specific nation.

### Information Sources ###

The concept of information sources is used throughout the application to link a domain object or a piece of information to the source it was retrieved from. So, when a character is read from an xml file, the character is associated with a XmlTurnInfoSource. If a character is read from a pdf file, the character is associated with a PdfTurnInfoSource. If a character is created by the user, it is associated with a UserInfoSource.

Most domain objects have an infoSource property.

### Persistent Storage ###

The game information is stored to the disk using the standard Java Serialization mechanism. No database is used. The Game is serialized to a disk file, and with it, all associated serializable information (metadata and turn information) is serialized.

This approach was chosen for its simplicity, as it requires minimum additional coding in order to save and load the domain model. However it has a number of implications:
  * Game file size increases in a linear manner with respect to the number of turns and number of nations in the game.
  * Likewise save and load times increase linearly.
  * Finally, at any one time the program has to load the entire game information in memory in order to access the game data.

## Reading Turns ##

Reading information from the files that contain the turn results is responsibility of the `org.joverseer.support.readers` package, and in particular of the `org.joverseer.support.readers.xml.TurnXmlReader` and `org.joverseer.support.readers.pdf.TurnPdfReader` (the former reads the xml files, the latter the pdf files). The Xml file reader uses Apache Digester to translate the xml into java objects, and then use these java objects to populate the game's domain model. The Pdf file reader first parses the pdf into an xml document, then uses Apache Digester to translate this xml document into java objects, and then uses these objects to augment the information already read from the xml files. **The Xml reader must precede the Pdf reader, otherwise the results are undefined - the pdf reader only works properly if the turn is already populated with information from the xml files**.

There is also the `NewXmlReader`, which was intended to read the new xml format, but is not finished (as neither is the new xml format).

There is also the `OrderFileReader` and `OrderTextReader`, which parse order files or order reports and adds the orders to the game information.

## User Interface ##

The user interface consists of mainly three parts:
  1. The Map
  1. The Current Hex View
  1. The Information Panes

### The Map ###

The map is implemented by class **org.joverseer.ui.map.MapPanel** which extends JPanel.

Integral to the workings of the map are the Renderers, in package `org.joverseer.ui.map.renderers`. Each renderer is responsible for drawing a specific type of objects on the map (i.e. the `DefaultHexRenderer` draws the hexes, the `PopulationCenterRenderer` draws the population centers). Additional renderers can be implemented to either replace the existing, or add new functionality.

### The Current Hex View ###

The Current Hex View pane is implemented by class **org.joverseer.ui.viewers.CurrentHexDataViewer**. Its layout is predefined, however it delegates the rendering of the actual information shown to the viewers. There is one viewer for each type of information (i.e. the `CharacterViewer` is responsible for showing information about a character, the `PopulationCenterViewer` is responsinble for showing information about the pop center).

Again new viewers can be implemented to replace or extend the existing ones.

### The Information Panes ###

These are implemented in package `org.joverseer.ui.listviews`. Most of the information panes extend two base classes the provide the standard functionality that most information panes share. These two base classes are **`ItemListView`** and **`ItemTableModel`**.
The `ItemListView` implements a View, which is a Spring Rich Client concept, but which is basically a window which contains a panel with a table and some filters (the combo boxes on top), and buttons). The `ItemTableModel` is a `BeanTableModel`, which extends the standard Java Table Model to automatically operate on Java Beans (i.e. java objects).

The standard functionality provided is:
  * The ability to easily define filters. Filters are the combo boxes and text box on the top part of the information panes that let the user filter the information shown.
  * The ability to double click on a line to center the map (provided the object implements the IHasMapLocation interface).
  * The ability to show the nation either by number or by nation name (provided the object implements the IBelongsToNation interface).

The standard functionality also allows the developer to easily implement additional information panes, by overriding some of the methods of `ItemListView` and `ItemTableModel`.

## The Application Context ##

The application context is a concept introduced by the Spring Right Client framework. Basically it is a set of xml files where the basic objects and windows of the application are defined. For instance, the map renderers are defined in these xml files. Also, all windows are defined there.

These xml files are located in the /resources/ctx folder. They are:
  * jideApplicationContext.xml: Defines the basic application beans
  * richclient-page-application-context.xml: Defines all the application views
  * preferences-context.xml: Defines the user preferences

Also, Spring externalizes most ui resources (images, labels and messages) to files. These are /resources/ui/messages.properties and /resources/ui/images.properties.

Also, the structure of the application menus is defined in /resources/ui/commands-context.xml.

## Preferences ##

JOverseer uses the standard Java preferences mechanism to store user preferences. In windows there are stored in the user's profile in the registry. However, JOverseer uses the org.joverseer.preferences package to wrap the standard mechanism.

Access to preferences is via tha `PreferenceRegistry` singleton class. The set of preferences is declared in the preferences-context.xml, so that each preference has a name and a default value, which is returned if no value is found in the registry.

Also, the `PreferenceRegistry` defines the required information to dynamically build the Preferences dialog which allows the user to set the preferences.

A few preferences (such as the last directory from which files were imported) are **Game specific**. These are not stored using the above mechanism, but using the **`GamePreference`** class, which provides static access to the game preferences. Game Preferences have no default value.

## Application Events ##

JOverseer heavily relies on Spring's Application Event mechanism for communication between different parts of the application. This allows for looser coupling of code.

Spring's event mechanism provides an application wide event bus which allows events to be published to registered subscribers.

Most important actions in JOverseer (such as loading a new game, changing the current turn, or editing an order) publish an event. This event is then handled by other components, each component doing whatever needs to be done.

For instance, when an order is changed, the `OrderChangedEvent` is fired. This event is handled by:
  * The Map View, to redraw the order, if needed
  * The Current Hex View, to refresh the order information shown, if needed
  * The Order Editor, to refresh the order being shown, if needed
  * The Economy Calculator, to update the cost of orders, if needed

The events are declared in `org.joverseer.ui.LifecycleEventsEnum`.