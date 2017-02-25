# Inventory
Create an app to track the inventory of a retail store, including current stock and supplier information.

1.Storing information in a SQLite database
2.Integrating Android’s file storage systems into that database
3.Presenting information from files and SQLite databases to users
4.Updating information based on user input.
5.Creating intents to other apps using stored information.

The listView populates with the current products stored in the table.
The Add product button prompts the user for information about the product and a picture, each of which are then properly stored in the table.
The modify quantity buttons in the detail view properly increase and decrease the quantity available for the correct product.
The delete button prompts the user for confirmation and, if confirmed, deletes the product record entirely and sends the user back to the main activity.
The ‘order more’ button sends an intent to an email app to contact the supplier using the information stored in the database.
