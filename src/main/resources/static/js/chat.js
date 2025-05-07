async function createChat() {
    const name = document.getElementById('chatName').value;
    const participants = Array.from(document.getElementById('chatParticipants').selectedOptions)
        .map(option => option.value);
    
    console.log('Selected participants:', participants);
    
    try {
        const checkResponse = await fetch(`/api/chats/check-name?name=${encodeURIComponent(name)}`);
        if (!checkResponse.ok) {
            throw new Error('Failed to check chat name');
        }
        
        const nameExists = await checkResponse.json();
        if (nameExists) {
            showToast('Error', 'A chat with this name already exists. Please choose a different name.', 'error');
            return;
        }
        

        const request = {
            name: name,
            userIds: participants 
        };
        
        console.log('Sending request:', request);
        
        const response = await fetch('/api/chats', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        });

        if (!response.ok) {
            throw new Error('Failed to create chat');
        }

        const chat = await response.json();
        showToast('Success', 'Chat created successfully!', 'success');
        
        const modal = bootstrap.Modal.getInstance(document.getElementById('newChatModal'));
        if (modal) {
            modal.hide();
        }
        
        document.getElementById('chatName').value = '';
        document.getElementById('chatParticipants').selectedIndex = -1;
        
        await loadChats();
        
        await selectChat(chat.id);
    } catch (error) {
        console.error('Error creating chat:', error);
        showToast('Error', 'Failed to create chat. Please try again.', 'error');
    }
}

async function deleteChat(chatId) {

    const modal = new bootstrap.Modal(document.getElementById('deleteMessageModal'));
        const confirmButton = document.getElementById('confirmDeleteMessage');
        
        const newConfirmButton = confirmButton.cloneNode(true);
        confirmButton.parentNode.replaceChild(newConfirmButton, confirmButton);
        
        newConfirmButton.addEventListener('click', async () => {
            try {
                const response = await fetch(`/api/chats/${chatId}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
        if (currentChatId === chatId) {
                 currentChatId = null;
                 document.getElementById('current-chat-name').textContent = 'Select a chat';
                 document.getElementById('chat-messages').innerHTML = '';
            }
                    showToast('Success', 'Chat deleted successfully', 'success');
                } else {
                    throw new Error('Failed to delete chat');
                }
            } catch (error) {
                console.error('Error deleting chat:', error);
                showToast('Error', 'Failed to delete chat', 'error');
            }
            
            modal.hide();
        });
        
        modal.show();
    }

    function pollChats() {
        fetch('/api/chats')
            .then(response => response.json())
            .then(chats => {
                console.log(chats);
                const chatsList = document.getElementById('chats-list');
              

                let hasNewChats = false;
                chats.forEach(chat => {
                    console.log("sdsda");
                    console.log(displayedChatIds);
                    if (!displayedChatIds.has(chat.id)) {
                        displayedChatIds.add(chat.id);
                      //  addChatToList(chat);
                        hasNewChats = true;
                    }
                 
                    if(chat.unreadMessages && chat.unreadMessages > 0){

                        const chatItem = document.querySelector(`.chat-item[data-chat-id="${chat.id}"]`);
                          console.log("sdsda2");
                        if (chatItem) {
                const unreadMessagesElement = chatItem.querySelector('.badge.bg-primary');
               if (unreadMessagesElement) {
                          const unreadMessages = parseInt(unreadMessagesElement.textContent.trim(), 10);
                         if(unreadMessages != chat.unreadMessages){
                          unreadMessagesElement.textContent = chat.unreadMessages;
                         }
           
             }
                    }
                }
            }
        )
            .catch(error => console.error('Error polling chats:', error));
    })
}



    async function startChat(friendId) {
        try {
            const response = await fetch(`/api/chats/private/${friendId}`);
            
            if (response.ok) {
                const chat = await response.json();
                await selectChat(chat.id);
            } else if (response.status === 404) {
                const createResponse = await fetch('/api/chats', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        name: 'Private Chat',
                        userIds: [friendId],
                        type: 'PRIVATE'
                    })
                });
                
                if (createResponse.ok) {
                    const chat = await createResponse.json();
                    await selectChat(chat.id);
                    showToast('Success', 'New chat created!', 'success');
                } else {
                    const errorData = await createResponse.text();
                    throw new Error(errorData || 'Failed to create chat');
                }
            } else {
                throw new Error('Failed to check for existing chat');
            }
        } catch (error) {
            console.error('Error starting chat:', error);
            showToast('Error', 'Failed to start chat. Please try again.', 'error');
        }
    }


    let chatParticipants = [];

    async function selectChat(chatId) {
        
            currentPage = 0;
            hasMoreMessages = true;
            displayedMessageIds.clear();
    
           
            if(!chatId){
                return;
        }
        currentChatId = chatId;

        try {
            markMessagesAsRead(chatId);
            switchSection('chat');
            await loadChatMembers(chatId);
      
    
            const chatItems = document.querySelectorAll('.chat-item');
            chatItems.forEach(item => {
                item.classList.remove('active');
                if (item.getAttribute('data-chat-id') === chatId) {
                    item.classList.add('active');
                }
            });


            markMessagesAsRead(chatId);
            
            displayedMessageIds.clear();
            
            document.getElementById('current-chat-name').textContent = 'Loading...';
            document.getElementById('chat-messages').innerHTML = '<div class="text-center p-3">Loading messages...</div>';

            const chatResponse = await fetch(`/api/chats/${chatId}`);
            if (!chatResponse.ok) {
                throw new Error('Failed to load chat details');
            }
            const chat = await chatResponse.json();
            console.log(chat);
            chatParticipants = chat.userIds;
            document.getElementById('current-chat-name').textContent = chat.name;
            
    
            const messagesDiv = document.getElementById('chat-messages');
            messagesDiv.innerHTML = '';
            
            const messagesResponse = await fetch(`/api/chats/${chatId}/messages?page=0&size=50`);
            if (!messagesResponse.ok) {
                throw new Error('Failed to load messages');
            }
            const messages = await messagesResponse.json();
            
            if (messages && messages.length > 0) {
                const chatItem = document.querySelector(`.chat-item[data-chat-id="${chatId}"]`);
if (chatItem) {
const unreadMessagesElement = chatItem.querySelector('.badge.bg-primary');
if (unreadMessagesElement) {
  unreadMessagesElement.textContent = 0;

}
}

   
                lastMessageCheck = Math.max(...messages.map(m => new Date(m.createdAt).getTime()));
                
                currentChatId = chatId;
                messages.forEach(message => {
                    displayMessage(message);
                });

                


                messagesDiv.scrollTop = messagesDiv.scrollHeight;
            } else {
                messagesDiv.innerHTML = '<div class="text-center p-3">No messages yet. Start the conversation!</div>';
            }


            const messageInput = document.getElementById('message-input');
            const sendButton = document.getElementById('send-button');
    

  
            sendButton.addEventListener('click', sendMessage);
            messageInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    sendMessage();
                }
            });

            // Load chat members
            await loadChatMembers(chatId);
      

        } catch (error) {
            console.error('Error selecting chat:', error);
            document.getElementById('current-chat-name').textContent = 'Error';
            document.getElementById('chat-messages').innerHTML = `
                <div class="text-center p-3 text-danger">
                    <i class="fas fa-exclamation-circle fa-2x mb-2"></i>
                    <p>Failed to load chat. Please try again.</p>
                    <button class="btn btn-sm btn-primary mt-2" onclick="selectChat('${chatId}')">
                        Retry
                    </button>
                </div>
            `;
            showToast('Error', 'Failed to load chat. Please try again.', 'error');
        }
    }

    async function loadChats() {
        try {
            console.log('Loading chats');
            const response = await fetch('/api/chats');
            const chats = await response.json();
            const chatList = document.querySelector('.chat-list');
            
            if (chatList) {
                chatList.innerHTML = chats.map(chat => `

                        <div class="chat-item" data-chat-id="${chat.id}" onclick="selectChat('${chat.id}')">
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="d-flex align-items-center gap-2">
                                <span class="chat-icon">${chat.type === 'PRIVATE' ? '<i class="fas fa-user"></i>' : '#'}</span>
                                <span class="chat-name">${chat.name}</span>
                            </div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="badge bg-primary">${chat.unreadMessages}</span>
                        
                                <button class="btn btn-sm btn-outline-danger" onclick="deleteChat('${chat.id}', event)">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                `).join('');
            }
        } catch (error) {
            console.error('Error loading chats:', error);
        }
    }


    
    // function initializeChat() {
    //     const messagesContainer = document.getElementById('chat-messages');
    //     const messageInput = document.getElementById('message-input');
    //     const sendButton = document.getElementById('send-button');

    //     currentPage = 0;
    //     hasMoreMessages = true;
    //     displayedMessageIds.clear();

  
    //     // loadInitialMessages();

    //     // messagesContainer.addEventListener('scroll', () => {
    //     //     if (messagesContainer.scrollTop === 0 && hasMoreMessages && !isLoadingMessages) {
    //     //         currentPage++;
    //     //         loadInitialMessages();
    //     //     }
    //     // });

    //     sendButton.addEventListener('click', sendMessage);
    //     messageInput.addEventListener('keypress', (e) => {
    //         if (e.key === 'Enter') {
    //             sendMessage();
    //         }
    //     });
    // }

    
    async function loadChatMembers(chatId) {
        fetch(`/api/chats/${chatId}/members`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to load chat members');
                }
                return response.json();
            })
            .then(members => {
                const membersContent = document.getElementById('members-content');
    
                if (!membersContent) {
                    console.warn('Members content element not found');
                    return;
                }
    
                if (!members || members.length === 0) {
                    membersContent.innerHTML = '<div class="text-center p-3">No members found</div>';
                    return;
                }
    
                // Получение ID участников
                const userIds = members.map(member => member.id);
    
                // Запрос статусов
                return fetch('/api/users/status', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(userIds)
                })
                .then(statusRes => {
                    if (!statusRes.ok) {
                        throw new Error('Failed to fetch user statuses');
                    }
                    return statusRes.json();
                })
                .then(statusMap => {
                    membersContent.innerHTML = members.map(member => {
                        const isOnline = statusMap[member.id] === true;
                        return `
                            <div class="member-item">
                                <img src="${member.pictureUrl || '/images/default-avatar.png'}" alt="${member.username}" class="member-avatar">
                                <div class="member-info">
                                    <div class="member-name">${member.fullName}</div>
                                    <div class="member-status ${isOnline ? 'online' : 'offline'}">${isOnline ? 'Online' : 'Offline'}</div>
                                </div>
                            </div>
                        `;
                    }).join('');
                });
            })
            .catch(error => {
                console.error('Error loading chat members:', error);
                showToast('Error', 'Failed to load chat members', 'error');
            });
    }
    
    