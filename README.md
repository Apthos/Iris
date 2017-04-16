# Iris
Hartnell Computer Science Toolset Server

# Examples:

*Send a Email*
```java
iMail mail = new iMail(this);
mail.setText("Hey");
mail.setSubject("Saying Hey");
mail.addRecipient("Kevin@SomeEmail.net", RecipeintType.TO);
mail.send();
```

*Send a Text*
```java
iText text = new iText(this);
text.setText("Hey");
text.addRecipient("80012345678");
text.send();
```
