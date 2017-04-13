# Iris
Hartnell Computer Science Toolset Server

# Examples:

*Send an Email*
```java
iMail = new iMail(this);
mail.setText("Hey");
mail.setSubject("Saying Hey");
mail.addRecipient("Kevin@SomeEmail.net", RecipeintType.TO);
mail.send();
```
