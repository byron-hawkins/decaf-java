package com.bitwise.umail;


class uMailPlugins 
{
    public void createMessage(com.bitwise.umail.jAccount fromAccount, com.bitwise.umail.jMessageInput messageInput) 
    {
        java.lang.String to;

        to = messageInput.getToPrompt().askUser();
        java.lang.String subject;

        subject = messageInput.getSubjectPrompt().askUser();
        java.lang.String body;

        body = messageInput.getBodyPrompt().askUser();
        com.bitwise.umail.jMessage message;

        message = messageInput.getMessage();
        message.setTo(to);

        if (subject.length() == 0) 
        {
            subject = "Message from " + fromAccount.getUser().getAddress();
        }
        else 
        {
            {
            }
        }
        message.setSubject(subject);
        message.setBody(body);
    }

    public void exit(com.bitwise.umail.jAccount currentAccount) 
    {
        java.lang.System.err.println("Thank you for using uMail.  Goodbye.");
    }

    public void displayMessage(com.bitwise.umail.jAccount currentAccount, com.bitwise.umail.jMessageDisplay messageDisplay) 
    {
        messageDisplay.init();
        messageDisplay.align();
        messageDisplay.getFrom().display();
        messageDisplay.getSentTime().display();
        messageDisplay.getSubject().display();
        java.lang.System.err.println("");
        messageDisplay.getBody().display();
    }

    public void getFolderMenu(com.bitwise.umail.jAccount currentAccount, com.bitwise.umail.jFolder currentFolder, com.bitwise.umail.jMenu thisMenu) 
    {
        java.util.Iterator messages;
        java.util.Iterator folders;

        messages = (java.util.Iterator)currentFolder.getMessages();
        folders = (java.util.Iterator)currentFolder.getSubFolders();
        while (messages.hasNext()) 
        {
            com.bitwise.umail.jMessage m;

            m = (com.bitwise.umail.jMessage)messages.next();
            if (currentFolder.getName().equalsIgnoreCase("sent items")) 
            {
                thisMenu.append(new com.bitwise.umail.jMessageMenuOption(m.getTo().getAddress(), m));
            }
            else 
            {
                {
                    thisMenu.append(new com.bitwise.umail.jMessageMenuOption(m.getFrom().getAddress(), m));
                }
            }
        }
        while (folders.hasNext()) 
        {
            com.bitwise.umail.jFolder f;

            f = (com.bitwise.umail.jFolder)folders.next();
            thisMenu.append(new com.bitwise.umail.jFolderMenuOption(f.getName(), f));
        }
        thisMenu.append(new com.bitwise.umail.jFolderMenuPlugin("Delete", currentFolder, "deleteFolderItem"));
    }

    public void sendMail(com.bitwise.umail.jAccount currentAccount, com.bitwise.umail.jMessage message) 
    {
        com.bitwise.umail.jFolder sentItems;

        sentItems = currentAccount.getRoot().getSubFolder("Sent Items", true);
        sentItems.add(message);
    }

    public void receiveMail(com.bitwise.umail.jAccount currentAccount, com.bitwise.umail.jMessage message) 
    {
        com.bitwise.umail.jFolder inbox;

        inbox = currentAccount.getRoot().getSubFolder("Inbox", true);
        inbox.add(message);
    }

    public void getPluginMenu(com.bitwise.umail.jAccount currentAccount, com.bitwise.umail.jMenu pluginMenu) 
    {
        pluginMenu.append(new com.bitwise.umail.jMenuOption("Delete Old Messages", "deleteOldMessages"));
    }

    public void deleteFolderItem(com.bitwise.umail.jAccount a, com.bitwise.umail.jFolder b) 
    {
        com.bitwise.umail.jPrompt itemPrompt;

        itemPrompt = new com.bitwise.umail.jPrompt("Select an item to delete: ", true);
        java.lang.String response;

        response = itemPrompt.askUser();
        int item;

        item = java.lang.Integer.parseInt(response);
        b.remove(item);
    }

    public void deleteOldMessages(com.bitwise.umail.jAccount a) 
    {
        com.bitwise.umail.jTime expiry;

        expiry = new com.bitwise.umail.jTime();
        expiry.add(com.bitwise.umail.jTime.MONTH, -1);
        java.lang.System.err.println("Deleting all messages sent before " + expiry.format());
        this.deleteOldMessages(a.getRoot(), expiry);
    }

    public void deleteOldMessages(com.bitwise.umail.jFolder folder, com.bitwise.umail.jTime expiry) 
    {
        java.util.Iterator messages;
        com.bitwise.umail.jMessage message;

        messages = (java.util.Iterator)folder.getMessages();
        while (messages.hasNext()) 
        {
            message = (com.bitwise.umail.jMessage)messages.next();
            if (message.getSentTime().before(expiry)) 
            {
                java.lang.System.err.println("Removing a message");
                messages.remove();
            }
            else 
            {
                {
                }
            }
        }
        java.util.Iterator folders;

        folders = (java.util.Iterator)folder.getSubFolders();
        while (folders.hasNext()) 
        {
            com.bitwise.umail.jFolder next;

            next = (com.bitwise.umail.jFolder)folders.next();
            this.deleteOldMessages(next, expiry);
        }
    }

}
