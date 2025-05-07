const sections = {
    activity: {
        title: 'Activity Feed',
        icon: 'fa-home',
        content: `
            <div class="activity-feed">
                <h4>Recent Activity</h4>
                <div id="activity-list" class="list-group">

                </div>
            </div>
        `
    },
    chat: {
        title: 'Chat',
        icon: 'fa-comment',
        content: `
            <div class="chat-section">
                <div class="chat-header">
                    <div>
                        <h4 id="current-chat-name">Select a chat</h4>

                    </div>
                    <div class="user-info">
                        <button class="btn btn-outline-primary btn-sm ms-2" id="toggle-members">
                            <i class="fas fa-users"></i>
                        </button>
                    </div>
                </div>
                <div class="chat-container">
                    <div class="chat-messages" id="chat-messages">

                    </div>
                    <div class="members-list" id="members-list">
                        <div class="members-header">
                            <h5>Members</h5>
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#addUsersModal">
                                <i class="fas fa-user-plus"></i>
                            </button>
                        </div>
                        <div class="members-content" id="members-content">
                                    </div>
                    </div>
                </div>
                <div class="message-input" id="message-input-area">
                    <div class="input-group">
                        <input type="text" class="form-control" id="message-input" placeholder="Type a message...">
                        <div class="input-group-append">
                            <button class="btn btn-outline-secondary" type="button" id="attach-button">
                                <i class="fas fa-paperclip"></i>
                            </button>
                            <button class="btn btn-primary" type="button" id="send-button">Send</button>
                        </div>
                    </div>
                    <div class="file-preview" id="file-preview"></div>
                    <input type="file" id="file-input" multiple style="display: none;" >
                    <div class="message-actions">
                        <button class="action-button" title="Format text">
                            <i id="paper-click" class="fas fa-bold"></i>
                        </button>
                        <button class="action-button" title="Add emoji">
                            <i class="far fa-smile"></i>
                        </button>
                        <button class="action-button" title="Add reaction">
                            <i class="far fa-thumbs-up"></i>
                        </button>
                    </div>
                </div>
            </div>
        `
    },
    friends: {
        title: 'Friends',
        icon: 'fa-user-friends',
        content: `
            <div class="friends-section">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4>Friends</h4>
                    <div>
                        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addFriendModal">
                            <i class="fas fa-user-plus"></i> Add Friend
                        </button>
                    </div>
                </div>

                 <div class="card mb-4">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Pending Friend Requests</h5>
                        <span class="badge bg-primary" id="pendingRequestsCount">0</span>
                    </div>
                    <div class="card-body p-0">
                        <div id="friendRequestList" class="list-group list-group-flush">
                          </div>
                    </div>
                </div>

                 <div class="card">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">My Friends</h5>
                        <span class="badge bg-primary" id="friendsCount">0</span>
                    </div>
                    <div class="card-body p-0">
                        <div class="search-container p-3 border-bottom">
                            <input type="text" class="form-control" id="friendSearch" placeholder="Search friends...">
                        </div>
                        <div class="friends-list" id="friendsList">
                         </div>
                    </div>
                </div>
            </div>
        `
    },
    profile: {
        title: 'Profile',
        icon: 'fa-user',
        content: `
            <div class="profile-section">
                <div class="card">
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <div class="profile-picture-container">
                                <img id="profilePicture" src="" alt="Profile Picture" class="profile-picture rounded-circle mb-3" style="width: 150px; height: 150px; object-fit: cover;">
                                <button class="btn btn-outline-primary btn-sm" onclick="document.getElementById('profilePictureInput').click()">
                                    <i class="fas fa-camera"></i> Change Picture
                                </button>
                                <input type="file" id="profilePictureInput" accept="image/*" style="display: none" onchange="handleProfilePictureChange(event)">
                            </div>
                        </div>
                        <form id="profileForm">
                           <div class="mb-3">
                    <label for="profileEmail" class="form-label">Email</label>
                    <input type="email" class="form-control" id="profileEmail" required>
                </div>
                <div class="mb-3">
                    <label for="profileFullName" class="form-label">Full Name</label>
                    <input type="text" class="form-control" id="profileFullName" required>
                </div>
                <div class="mb-3">
                    <label for="profileBio" class="form-label">Bio</label>
                    <textarea class="form-control" id="profileBio" rows="3"></textarea>
                </div>
                            <div class="mb-3">
                                <label for="profileStatus" class="form-label">Status</label>
                                <select class="form-select" id="profileStatus">
                                    <option value="online">Online</option>
                                    <option value="away">Away</option>
                                    <option value="offline">Offline</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="currentPassword" class="form-label">Current Password</label>
                                <input type="password" class="form-control" id="currentPassword">
                            </div>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">New Password</label>
                                <input type="password" class="form-control" id="newPassword">
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                <input type="password" class="form-control" id="confirmPassword">
                            </div>
                            <button type="button" class="btn btn-primary" onclick="updateProfile()">Save Changes</button>
                        </form>
                    </div>
                </div>
            </div>
        `
    }
};

const mainContent = document.querySelector('.main-content');
let currentSection = 'activity';


function switchSection(sectionId) {
    if(currentSection == 'chat' && sectionId == 'chat'){
        return;
    }
    document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
    document.querySelector(`.nav-item[data-section="${sectionId}"]`).classList.add('active');
    uncheckActiveChatItems() ;

    const section = sections[sectionId];
    mainContent.innerHTML = section.content;

    const chatHeader = document.querySelector('.chat-header h4');
    if (chatHeader) {
        chatHeader.textContent = section.title;
    }


    loadSectionContent(sectionId);
    currentSection = sectionId;
}

function loadSectionContent(sectionId) {
    switch (sectionId) {
        case 'chat':
            selectChat();
            break;
        case 'friends':
            loadFriends();
            loadFriendRequests();
            break;
        case 'profile':
            loadUserProfile();
            break;
    }
}

function uncheckActiveChatItems() {
    const chatItems = document.querySelectorAll('.chat-item.active');
    chatItems.forEach(item => item.classList.remove('active'));
}

 document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', () => {
        const sectionId = item.getAttribute('data-section');
        switchSection(sectionId);
    });
});
