
function displayMessage(message, scrollToDown) {
    const messagesContainer = document.getElementById('chat-messages');
if (!messagesContainer) {
    console.error('Messages container not found');
    return;
}

const isCurrentUser = message.senderId === currentUser.id;
const messageId = `message-${message.id}`;
const existingElement = document.getElementById(messageId);
if (existingElement) {

    if (isCurrentUser) {
        const alreadyHasDoubleCheck = existingElement.querySelector('.fa-check-double');
        const everyoneRead = message.readBy && chatParticipants && message.readBy.length === chatParticipants.length;
        console.log(message.readBy.length + " "+ chatParticipants.length);
        if (!alreadyHasDoubleCheck && everyoneRead) {
            const readIndicator = document.createElement('div');
            readIndicator.className = 'read-indicator';
            readIndicator.innerHTML = '<i class="fas fa-check-double" style="color:blue"></i>';
            existingElement.appendChild(readIndicator);
        }
    }

if(existingElement.querySelector('.message-text').textContent != message.content){
existingElement.querySelector('.message-text').textContent = message.content;
}
    return;
}

const messageElement = document.createElement('div');
messageElement.className = `message ${isCurrentUser ? 'sent' : 'received'}`;
messageElement.id = messageId;

const messageContent = document.createElement('div');
messageContent.className = 'message-content';

const messageHeader = document.createElement('div');
messageHeader.className = 'message-header';
messageHeader.innerHTML = `
    <span class="sender-name">${message.senderUsername}</span>
    <span class="message-time">${formatMessageTime(message.createdAt)}</span>
`;
messageContent.appendChild(messageHeader);

const messageText = document.createElement('div');
messageText.className = 'message-text';
messageText.textContent = message.content;
messageContent.appendChild(messageText);

const actionsDiv = document.createElement('div');
actionsDiv.className = 'message-actions';

   const deleteButton = document.createElement('button');
deleteButton.className = isCurrentUser ? 'btn btn-sm btn-outline-danger' : 'delete-message-btn';
deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
deleteButton.onclick = () => deleteMessage(message.id);
actionsDiv.appendChild(deleteButton);
        

if (isCurrentUser) {
 

const editButton = document.createElement('button');
        editButton.className = 'edit-message';
        editButton.innerHTML = '<i class="fas fa-edit"></i>';
        editButton.onclick = () => editMessage(message.id);
        actionsDiv.appendChild(editButton);

    const everyoneRead = message.readBy && chatParticipants && message.readBy.length === chatParticipants.length;
    const readIndicator = document.createElement('div');
    readIndicator.className = 'read-indicator';
    readIndicator.innerHTML = everyoneRead
        ? '<i class="fas fa-check-double" style="color:blue"></i>'
        : '<i class="fas fa-check"></i>';
    messageContent.appendChild(readIndicator);
}

messageContent.appendChild(actionsDiv);

  if (message.fileAttachments && message.fileAttachments.length > 0) {
        const attachmentsContainer = document.createElement('div');
        attachmentsContainer.className = 'file-attachments';

        message.fileAttachments.forEach(attachment => {
            const attachmentElement = document.createElement('div');
            attachmentElement.className = 'file-attachment';

            const fileIcon = document.createElement('i');
            fileIcon.className = 'file-icon fas ' + getFileIcon(attachment.mimeType);

            const fileName = document.createElement('a');
            fileName.href = attachment.fileUrl;
            fileName.download = attachment.fileName;
            fileName.textContent = attachment.fileName;
            fileName.className = 'file-name';

            const fileSize = document.createElement('span');
            fileSize.className = 'file-size';
            fileSize.textContent = formatFileSize(attachment.fileSize);

            attachmentElement.appendChild(fileIcon);
            attachmentElement.appendChild(fileName);
            attachmentElement.appendChild(fileSize);
            attachmentsContainer.appendChild(attachmentElement);
        });

        messageContent.appendChild(attachmentsContainer);
    }
messageElement.appendChild(messageContent);

const prevScrollHeight = messagesContainer.scrollHeight;
const prevScrollTop = messagesContainer.scrollTop;

let inserted = false;
const existingMessages = messagesContainer.children;
if(existingMessages.length > 0){
for (let i = 0; i < existingMessages.length; i++) {
    const existingMessage = existingMessages[i];
    if(existingMessage.querySelector('.message-time')){

    
    const existingTime = new Date(existingMessage.querySelector('.message-time').textContent).getTime();
    const newTime = new Date(message.createdAt).getTime();

    if (newTime < existingTime) {
        messagesContainer.insertBefore(messageElement, existingMessage);
        inserted = true;
        break;
    }}
}
}
const newScrollHeight = messagesContainer.scrollHeight;
messagesContainer.scrollTop = prevScrollTop + (newScrollHeight - prevScrollHeight);

if (!inserted) {
    messagesContainer.appendChild(messageElement);
}

}


async function sendMessage() {
    if (isSendingMessage) return;
    
    const messageInput = document.getElementById('message-input');
    const content = messageInput.value.trim();
    
    if (!content && selectedFiles.length === 0) return;

    try {
        isSendingMessage = true;
        
        const formData = new FormData();
        formData.append('content', content);
        selectedFiles.forEach(file => {
            formData.append('files', file);
        });

        const response = await fetch(`/api/chats/${currentChatId}/messages`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Failed to send message');
        }

        const newMessage = await response.json();

        if (!displayedMessageIds.has(newMessage.id)) {
            const messageElement = displayMessage(newMessage);
            document.querySelector('.chat-messages').appendChild(messageElement);
            displayedMessageIds.add(newMessage.id);
            if (isAtBottom()) {
                        scrollToBottom();
                    }
        }

        // Clear the input and files
        messageInput.value = '';
        selectedFiles = [];
        updateFilePreview();
        
    } catch (error) {
        console.error('Error sending message:', error);
        showToast('Error sending message', 'error');
    } finally {
        isSendingMessage = false;
    }
}



function deleteMessage(messageId) {
    if (!currentChatId) return;
    
    if (!confirm('Are you sure you want to delete this message?')) {
        return;
    }
    
    fetch(`/api/chats/${currentChatId}/messages/${messageId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete message');
        }
        const messageElement = document.getElementById(`message-${messageId}`);
        if (messageElement) {
            messageElement.remove();
        }
        displayedMessageIds.delete(messageId);
    })
    .catch(error => {
        console.error('Error deleting message:', error);
        showToast('Error', 'Failed to delete message', 'error');
    });
}

function formatMessageTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { 
        hour: '2-digit', 
        second: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour12: true 
    });
}


function loadInitialMessages() {
    if (!currentChatId || isLoadingMessages) return;
    isLoadingMessages = true;
    fetch(`/api/chats/${currentChatId}/messages?page=${currentPage}&size=${MESSAGES_PER_PAGE}`)
        .then(response => response.json())
        .then(messages => {
            if (messages.length === 0) {
                hasMoreMessages = false;
                return;
            }

            const messagesContainer = document.getElementById('chat-messages');
            const isInitialLoad = currentPage === 0;

            if (isInitialLoad) {
                messagesContainer.innerHTML = '';
                displayedMessageIds.clear();
            }

            messages.forEach(message => {
                    displayMessage(message, true);
                    displayedMessageIds.add(message.id);
            });


        })
        .catch(error => console.error('Error loading messages:', error))
        .finally(() => {
            isLoadingMessages = false;
        });
}



function pollMessagesForSpecificChat() {
    if (!currentChatId || isLoadingMessages) return;
    console.table("currentChatId");
    markMessagesAsRead(currentChatId);
    fetch(`/api/chats/${currentChatId}/messages?page=0&size=51`)
        .then(response => response.json())
        .then(messages => {
            if (!messages || messages.length === 0) return;

            const messagesContainer = document.getElementById('chat-messages');
            const wasAtBottom = isAtBottom(messagesContainer);
            let hasNewMessages = false;

            messages.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));

            messages.forEach(message => {

                    displayMessage(message);
                    if (!displayedMessageIds.has(message.id)) {
                    displayedMessageIds.add(message.id);
                    hasNewMessages = true;
                    }
                 
            });

            if (hasNewMessages) {
                if (wasAtBottom) {
                    messagesContainer.scrollTop = messagesContainer.scrollHeight;
                }
            }
        })
        .catch(error => console.error('Error polling messages:', error));
}

function updateMessageReadIndicator(messageId) {
    const messageElement = document.querySelector(`[data-message-id="${messageId}"]`);
    if (!messageElement) return;

    const status = messageReadStatus.get(messageId);
    if (!status) return;

    const readIndicator = messageElement.querySelector('.read-indicator') || 
        document.createElement('div');
    readIndicator.className = 'read-indicator';

    if (status.seenBy.size > 0) {
        readIndicator.innerHTML = `
            <i class="fas fa-check-double"></i>
            <span class="read-count">${status.seenBy.size}</span>
        `;
        readIndicator.title = `Seen by ${status.seenBy.size} user${status.seenBy.size > 1 ? 's' : ''}`;
    } else {
        readIndicator.innerHTML = '<i class="fas fa-check" style="color:blue"></i>';
        readIndicator.title = 'Delivered';
    }

    if (!messageElement.querySelector('.read-indicator')) {
        messageElement.appendChild(readIndicator);
    }
}



async function markMessagesAsRead(chatId) {

    try {
        const response = await fetch(`/api/chats/${chatId}/messages/read`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to mark messages as read');
        }
    } catch (error) {
        console.error('Error marking messages as read:', error);
    }
}



async function editMessage(message) {
    const messageId = `message-${message}`;
    const existingElement = document.getElementById(messageId);
    const messageActions = existingElement.querySelector(".message-actions");
    if (messageActions) {
messageActions.style.display = 'none';
}

    console.log("messageElement");
    if (!existingElement) return;
    console.log("messageElement");
    const contentContainer = existingElement.querySelector('.message-content');
    const textElement = contentContainer.querySelector('.message-text');
    

    const editForm = document.createElement('form');
    editForm.className = 'edit-message-form';
    
    const textarea = document.createElement('textarea');
    textarea.value = textElement.textContent || '';
    textarea.rows = 3;
    textarea.required = true;
    
    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'edit-buttons';
    
    const saveButton = document.createElement('button');
    saveButton.type = 'submit';
    saveButton.className = 'btn btn-primary btn-sm';
    saveButton.textContent = 'Save';
    
    const cancelButton = document.createElement('button');
    cancelButton.type = 'button';
    cancelButton.className = 'btn btn-secondary btn-sm';
    cancelButton.textContent = 'Cancel';
    cancelButton.onclick = () => {
        editForm.remove();
        textElement.style.display = 'block';
        if (messageActions) {
messageActions.style.display = 'block';
}
    };
    
    buttonContainer.appendChild(saveButton);
    buttonContainer.appendChild(cancelButton);
    
    editForm.appendChild(textarea);
    editForm.appendChild(buttonContainer);
    
    

    textElement.style.display = 'none';
    contentContainer.insertBefore(editForm, textElement);
    

    textarea.focus();
    

    editForm.onsubmit = async (e) => {
        if (messageActions) {
messageActions.style.display = 'block';
}
        e.preventDefault();
        
        try {
            const response = await fetch(`/api/chats/${currentChatId}/messages/${message}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    content: textarea.value
                })
            });
            
            if (!response.ok) {
                throw new Error('Failed to update message');
            }
            

            textElement.textContent = textarea.value;
            textElement.style.display = 'block';
            editForm.remove();
            
            

            showNotification('Message updated successfully', 'success');
        } catch (error) {
            console.error('Error updating message:', error);
            showNotification('Failed to update message', 'error');
        }
    };
}
