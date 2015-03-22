# Introduction #

JOverseer offers facilities for automatically importing orders. Orders can be imported from
  * Automagic order files (the files that Automagic generates for sending your turn)
  * The Automatic text from the "Send" worksheet (or the same text sent by email)

# Importing from text #

In order to import orders from text, the text must follow the format used in the Automagic "Send" worksheet. The text may contain additional lines, such as comments, but the order lines must follow the exact format. An example of such text turn is given below.

```
Faelathon (faela) @ 3013 (C39)
300  ChTaxRt  60
325  NatSell  le  80
Changes the tax rate and sells 80% of leather.

Bordan (borda) @ 3111 (C53)
215  RfsPers
810  MovChar  3519
Moves to meet Elrond.

Elberon (elber) @ 3217 (C44, E33)
525  InfOthr
870  MovJoin  3013  faela
Joins Faleathon's army to act as a backup commander.
```

To import such text, you must use the "Import Orders from Text" menu option. This option brings up the following form:

![http://joverseer.googlecode.com/svn/wiki/images/importorders.import-from-text-form.jpg](http://joverseer.googlecode.com/svn/wiki/images/importorders.import-from-text-form.jpg)

You should copy & paste the text that contains the orders in the left panel. Then you should click the "Parse" button. This button parses the text and populates the right panel with messages explaining whether JOverseer was able to parse the text. For instance, in the figure above, JOverseer identified 3 characters and 6 orders, and also will ignore several lines, including those with comments. However, it was not able to verify the character names because the orders are from a different game. As a result no orders will be imported. In a normal case, the orders will be imported successfully.

After viewing the parse results, if you are satisfied, click the "OK" button to actually import the orders.

# Importing from an Automagic file #

To Do