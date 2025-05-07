function showNotification(tabName) {
    const tab = document.querySelector(`[data-tab="${tabName}"]`);
    if (!tab) return;

    const badge = tab.querySelector('.notification-badge');
    if (badge) {
        badge.style.display = 'flex';
        badge.textContent = '•';
    } else {
        const newBadge = document.createElement('div');
        newBadge.className = 'notification-badge';
        newBadge.textContent = '•';
        newBadge.style.display = 'flex';
        tab.appendChild(newBadge);
    }
}

function clearNotification(tabName) {
    const tab = document.querySelector(`[data-tab="${tabName}"]`);
    if (!tab) return;

    const badge = tab.querySelector('.notification-badge');
    if (badge) {
        badge.style.display = 'none';
    }
}



   
function showNotification(notification) {
    if (notification.type === 'FRIEND_REQUEST') {

        if (currentSection === 'friends') {
            loadFriendRequests();
        } else {

            showToast('New Friend Request', `${notification.senderName} sent you a friend request. Check the Friends tab.`, 'info');
        }
    } else {

        showToast(notification.title, notification.message, notification.type.toLowerCase());
    }
}

function showToast(title, message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'error' ? 'danger' : type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <strong>${title}</strong><br>
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    document.body.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast, { delay: 5000 });
    bsToast.show();
    
    toast.addEventListener('hidden.bs.toast', () => toast.remove());
}