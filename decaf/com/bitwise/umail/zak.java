/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This prototype of the Decaf Java development environment is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This prototype is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package com.bitwise.umail;


class zak 
{
    public void createMessage(com.bitwise.umail.jAccount fromAccount, com.bitwise.umail.jMessageInput messageInput) 
    {
        java.lang.String to;

        to = messageInput.getToPrompt().askUser();

        if (to.equalsIgnoreCase("z.")) 
        {
            to = "zorgon";
        }
        else 
        {
            {
            }
        }
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
        java.lang.System.err.println("Later, dude.");
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
                    thisMenu.append(new com.bitwise.umail.jMessageMenuOption(m.getFrom().getAddress() + ": " + m.getSubject(), m));
                }
            }
        }
        while (folders.hasNext()) 
        {
            com.bitwise.umail.jFolder f;

            f = (com.bitwise.umail.jFolder)folders.next();
            java.lang.String display;

            display = f.getName() + " (" + java.lang.String.valueOf(f.getSize()) + ")";
            thisMenu.append(new com.bitwise.umail.jFolderMenuOption(display, f));
        }
        thisMenu.append(new com.bitwise.umail.jFolderMenuPlugin("Delete", currentFolder, "deleteFolderItem"));
    }

    public void receiveMail(com.bitwise.umail.jAccount currentAccount, com.bitwise.umail.jMessage message) 
    {
        com.bitwise.umail.jFolder inbox;

        inbox = currentAccount.getRoot().getSubFolder("Inbox", true);
        if (message.getFrom().getAddress().equalsIgnoreCase("zorgon")) 
        {
            com.bitwise.umail.jFolder zorgon;

            zorgon = inbox.getSubFolder("zorgon", true);
            zorgon.add(message);
        }
        else 
        {
            {
                inbox.add(message);
            }
        }
    }

}
