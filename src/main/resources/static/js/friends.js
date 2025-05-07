async function loadFriendRequests() {
    try {
        console.log('Fetching friend requests...');
        const response = await fetch('/api/friends/pending');
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const requests = await response.json();
        console.log('Raw friend requests:', requests);
        
        const requestList = document.getElementById('friendRequestList');
        const pendingRequestsCount = document.getElementById('pendingRequestsCount');
        
        if (requestList) {
            if (!requests || requests.length === 0) {
                requestList.innerHTML = `
                    <div class="text-center p-3">
                        <i class="fas fa-user-friends fa-2x mb-2"></i>
                        <p>No pending friend requests</p>
                    </div>
                `;
            } else {
            const tempContainer = document.createElement('div');
                 requests.forEach(request => {
                            const avatarUrl = request.pictureUrl || '/images/default-avatar.png';
                            const displayName = request.username;

                            const existingItem = requestList.querySelector(`[data-request-id="${request.id}"]`);
                            const requestHtml = `
                                <div class="list-group-item" data-request-id="${request.id}">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="d-flex align-items-center gap-3">
                                            <img src="${avatarUrl}"
                                                 alt="User"
                                                 class="friend-avatar rounded-circle"
                                                 style="width: 40px; height: 40px;">
                                            <div>
                                                <div class="fw-bold">${displayName}</div>
                                                <small class="text-muted">Friend Request</small>
                                            </div>
                                        </div>
                                        <div class="d-flex gap-2">
                                            <button class="btn btn-sm btn-success" onclick="handleFriendRequest('${request.id}', true, this)">
                                                <i class="fas fa-check"></i> Accept
                                            </button>
                                            <button class="btn btn-sm btn-danger" onclick="handleFriendRequest('${request.id}', false, this)">
                                                <i class="fas fa-times"></i> Reject
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            `;

                            if (existingItem) {
                                existingItem.outerHTML = requestHtml;
                            } else {
                                tempContainer.insertAdjacentHTML('beforeend', requestHtml);
                            }
                        });

                        while (tempContainer.firstChild) {
                            requestList.appendChild(tempContainer.firstChild);
                        }
                    }
                }

                if (pendingRequestsCount) {
                    pendingRequestsCount.textContent = requests ? requests.length : 0;
                }

                return requests;
    } catch (error) {
        console.error('Error loading friend requests:', error);
        const requestList = document.getElementById('friendRequestList');
        if (requestList) {
            requestList.innerHTML = `
                <div class="text-center p-3 text-danger">
                    <i class="fas fa-exclamation-circle fa-2x mb-2"></i>
                    <p>Failed to load friend requests. Please try again later.</p>
                </div>
            `;
        }
        return [];
    }
}

async function handleFriendRequest(friendId, accept, buttonElement) {
    try {
        const action = accept ? 'accept' : 'reject';
        const response = await fetch(`/api/friends/${action}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: friendId // Send friendId as raw string
        });

        if (response.ok) {
            showToast('Success', `Friend request ${accept ? 'accepted' : 'rejected'} successfully!`, 'success');
            
            // Remove the request from the list
            const requestItem = buttonElement.closest('.list-group-item');
            if (requestItem) {
                requestItem.remove();
            }
            
            // If accepted, refresh the friends list
            if (accept) {
                loadFriends();
            }
            
            // Refresh the requests list and update badge
            await loadFriendRequests();
            
            // Update the notification badge
            const badge = document.getElementById('friendRequestBadge');
            if (badge) {
                const remainingRequests = document.querySelectorAll('.list-group-item').length;
                if (remainingRequests > 0) {
                    badge.textContent = remainingRequests;
                    badge.classList.add('show');
                } else {
                    badge.classList.remove('show');
                }
            }
        } else {
            const errorData = await response.text();
            showToast('Error', errorData || `Failed to ${accept ? 'accept' : 'reject'} friend request`, 'error');
        }
    } catch (error) {
        console.error(`Error ${accept ? 'accepting' : 'rejecting'} friend request:`, error);
        showToast('Error', `Failed to ${accept ? 'accept' : 'reject'} friend request. Please try again.`, 'error');
    }
}

let availableUsers = [];

// Load available users
async function loadAvailableUsers() {
    try {
        const response = await fetch('/api/users');
        availableUsers = await response.json();
        updateUserLists();
    } catch (error) {
        console.error('Error loading users:', error);
    }
}


function updateUserLists() {
    const newChatUserList = document.getElementById('newChatUserList');
    const addUsersList = document.getElementById('addUsersList');
    
    if (newChatUserList) {
        newChatUserList.innerHTML = '';
        availableUsers.forEach(user => {
            const userItem = document.createElement('div');
            userItem.className = 'user-item';
            userItem.textContent = user.username;
            userItem.onclick = function() {
                this.classList.toggle('selected');
            };
            newChatUserList.appendChild(userItem);
        });
    }
    
    if (addUsersList) {
        addUsersList.innerHTML = '';
        availableUsers.forEach(user => {
            const userItem = document.createElement('div');
            userItem.className = 'user-item';
            userItem.textContent = user.username;
            userItem.onclick = function() {
                this.classList.toggle('selected');
            };
            addUsersList.appendChild(userItem);
        });
    }
}
    // Load participants for chat creation
    async function loadParticipants() {
        try {
            const response = await fetch('/api/users');
            const users = await response.json();
            const select = document.getElementById('chatParticipants');
            if (select) {
                select.innerHTML = '';
                users.forEach(user => {
                    const option = document.createElement('option');
                    option.value = user.id;
                    option.textContent = user.username;
                    select.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error loading participants:', error);
        }
    }

       // Load friends
       async function loadFriends() {
        try {
            const response = await fetch('/api/friends');
            const friends = await response.json();
            updateFriendsList(friends);
        } catch (error) {
            console.error('Error loading friends:', error);
        }
    }


    async function searchUsers(query) {
        if (!query || query.length < 2) {
            alert('Please enter at least 2 characters to search');
            return;
        }

        try {
            const response = await fetch(`/api/users/search?email=${encodeURIComponent(query)}`);
            if (!response.ok) {
                throw new Error('Search failed');
            }
            const users = await response.json();
            const searchResults = document.getElementById('searchResults');
            
            if (searchResults) {
                if (users.length === 0) {
                    searchResults.innerHTML = `
                        <div class="text-center p-3">
                            <i class="fas fa-search fa-2x mb-2"></i>
                            <p>No users found matching your search</p>
                        </div>
                    `;
                    return;
                }
                const pendingRequestsResponse = await fetch('/api/friends/pending');
                const pendingRequests = await pendingRequestsResponse.json();
                const pendingRequestIds = pendingRequests.map(request => request.id);

                searchResults.innerHTML = users.map(user => {
                    const isRequestSent = pendingRequestIds.includes(user.id);
                    const buttonClass = isRequestSent ? 'btn-success' : 'btn-primary';
                    const buttonText = isRequestSent ? 'Invite Sent' : 'Send Invite';
                    const buttonDisabled = isRequestSent ? 'disabled' : '';
                    const buttonIcon = isRequestSent ? 'fa-check' : 'fa-user-plus';

                    return `
                        <div class="user-item">
                            <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center gap-3">
                                    <img src="${user.pictureUrl || '/images/default-avatar.png'}" alt="${user.username}" class="friend-avatar">
                                    <div>
                                        <span class="friend-name">${user.username}</span>
                                      <div class="friend-status">
                                          <span class="user-presence ${user.online ? 'online' : 'offline'}"></span>
                                          ${user.online ? 'Online' : 'Offline'}
                                      </div>
                                        <small class="text-muted">${user.email}</small>
                                    </div>
                                </div>
                                <div class="user-actions">
                                    <button class="btn btn-sm ${buttonClass}" 
                                            onclick="sendFriendRequest('${user.id}', this)"
                                            ${buttonDisabled}>
                                        <i class="fas ${buttonIcon}"></i> ${buttonText}
                                    </button>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('');
            }
        } catch (error) {
            console.error('Error searching users:', error);
            const searchResults = document.getElementById('searchResults');
            if (searchResults) {
                searchResults.innerHTML = `
                    <div class="text-center p-3 text-danger">
                        <i class="fas fa-exclamation-circle fa-2x mb-2"></i>
                        <p>Failed to search users. Please try again.</p>
                    </div>
                `;
            }
        }
    }

    

//    async function loadPendingFriendRequests() {
//        try {
//            const response = await fetch('/api/friends/pending');
//            if (response.ok) {
//                const requests = await response.json();
//                requests.forEach(request => {
//                    showNotification({
//                        type: 'FRIEND_REQUEST',
//                        senderName: request.senderName,
//                        senderId: request.senderId
//                    });
//                });
//            }
//        } catch (error) {
//            console.error('Error loading pending friend requests:', error);
//        }
//    }
//
//    document.addEventListener('DOMContentLoaded', function() {
//
//        loadPendingFriendRequests();
//    });

      document.addEventListener('DOMContentLoaded', function() {
        const friendSearch = document.getElementById('friendSearch');
        if (friendSearch) {
            friendSearch.addEventListener('input', function() {
                const searchTerm = this.value.toLowerCase();
                const friendItems = document.querySelectorAll('.friend-item');
                
                friendItems.forEach(item => {
                    const friendName = item.querySelector('.friend-name').textContent.toLowerCase();
                    if (friendName.includes(searchTerm)) {
                        item.style.display = '';
                    } else {
                        item.style.display = 'none';
                    }
                });
            });
        }
    });


  let lastFriendRequestCheck = 0;
  let pollingInterval = 5000; 

  async function checkFriendRequests() {
      try {
          const response = await fetch('/api/friends/pending');
          if (!response.ok) {
              throw new Error('Failed to fetch friend requests');
          }
          const requests = await response.json();
          
          const badge = document.getElementById('friendRequestBadge');
          if (badge) {
              if (requests && requests.length > 0) {
                  badge.textContent = requests.length;
                  badge.classList.add('show');
              } else {
                  badge.classList.remove('show');
              }
          }
          
          if (currentSection === 'friends') {
              loadFriendRequests();
          }
      } catch (error) {
          console.error('Error checking friend requests:', error);
      }
  }



  async function sendFriendRequest(userId, buttonElement) {
      try {
          buttonElement.disabled = true;
          const response = await fetch('/api/friends/request', {
              method: 'POST',
              headers: {
                  'Content-Type': 'text/plain'
              },
              body: userId 
          });

          if (response.ok) {
              showToast('Success', 'Friend request sent successfully!', 'success');
              buttonElement.textContent = 'Invite Sent';
              buttonElement.classList.remove('btn-primary');
              buttonElement.classList.add('btn-success');
          } else {
              const errorData = await response.text();
              if (errorData.includes('already exists')) {
                  showToast('Warning', 'A friend request already exists with this user', 'warning');
                  buttonElement.textContent = 'Request Pending';
                  buttonElement.classList.remove('btn-primary');
                  buttonElement.classList.add('btn-warning');
              } else {
                  showToast('Error', errorData || 'Failed to send friend request', 'error');
                  buttonElement.disabled = false;
              }
          }
      } catch (error) {
          console.error('Error sending friend request:', error);
          showToast('Error', 'Failed to send friend request. Please try again.', 'error');
          buttonElement.disabled = false;
      }
  }

  
async function removeFriend(friendId) {
    if (confirm('Are you sure you want to remove this friend?')) {
        try {
            const response = await fetch(`/api/friends/${friendId}`, {
                method: 'DELETE'
            });
            if (response.ok) {
                loadFriends();
            }
        } catch (error) {
            console.error('Error removing friend:', error);
        }
    }
}

    function updateFriendsList(friends) {
        const friendsList = document.getElementById('friendsList');
        const friendsCount = document.getElementById('friendsCount');
        const userIds = friends.map(friend => friend.id);
    
        fetch('/api/users/status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userIds)
        })
        .then(response => response.json())
        .then(statusMap => {
            console.table(statusMap);
    
            if (friendsList) {
                if (!friends || friends.length === 0) {
                    friendsList.innerHTML = `
                        <div class="text-center p-3">
                            <i class="fas fa-user-friends fa-2x mb-2"></i>
                            <p>No friends yet. Add some friends to start chatting!</p>
                        </div>
                    `;
                } else {
                    friendsList.innerHTML = friends.map(friend => `
                        <div class="friend-item">
                            <div class="friend-info">
                                <img src="${friend.pictureUrl || '/images/default-avatar.png'}" alt="${friend.username}" class="friend-avatar">
                                <div class="friend-details">
                                    <span class="friend-name">${friend.username}</span>
                                    <span class="friend-status">
                                        <span class="user-presence ${statusMap[friend.id] ? 'online' : 'offline'}"></span>
                                        ${statusMap[friend.id] ? 'Online' : 'Offline'}
                                    </span>
                                </div>
                            </div>
                            <div class="friend-actions">
                                <button class="btn btn-sm btn-outline-primary" onclick="startChat('${friend.id}')" title="Start Chat">
                                    <i class="fas fa-comment"></i>
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="removeFriend('${friend.id}')" title="Remove Friend">
                                    <i class="fas fa-user-minus"></i>
                                </button>
                            </div>
                        </div>
                    `).join('');
                }
            }
    
            if (friendsCount) {
                friendsCount.textContent = friends ? friends.length : 0;
            }
        })
        .catch(error => {
            console.error('Failed to fetch user statuses:', error);
        });
    }