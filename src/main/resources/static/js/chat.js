/* ===========================
   BEAM - Chat Application JS
   =========================== */

const API_URL = window.location.origin;

class ChatApp {
    constructor() {
        this.ws = null;
        this.currentRoom = 'general';
        this.token = localStorage.getItem('token');
        this.username = localStorage.getItem('username') || 'Guest';
        this.displayName = localStorage.getItem('displayName') || '게스트';
        this.isTyping = false;
        this.typingTimeout = null;

        this.init();
    }

    init() {
        // Check authentication
        if (!this.token) {
            window.location.href = '/';
            return;
        }

        // Set user info
        this.updateUserInfo();

        // Connect WebSocket
        this.connectWebSocket();

        // Setup event listeners
        this.setupEventListeners();

        // Load initial data
        this.loadConversations();
    }

    updateUserInfo() {
        const userInfoEl = document.getElementById('currentUser');
        if (userInfoEl) {
            userInfoEl.textContent = this.displayName;
        }
    }

    connectWebSocket() {
        const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${wsProtocol}//${window.location.host}/ws?token=${this.token}`;

        this.ws = new WebSocket(wsUrl);

        this.ws.onopen = () => {
            console.log('WebSocket connected');
            this.showNotification('연결되었습니다', 'success');
            this.joinRoom(this.currentRoom);
        };

        this.ws.onmessage = (event) => {
            const message = JSON.parse(event.data);
            this.handleMessage(message);
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.showNotification('연결 오류가 발생했습니다', 'error');
        };

        this.ws.onclose = () => {
            console.log('WebSocket disconnected');
            this.showNotification('연결이 끊어졌습니다. 재연결 중...', 'warning');
            setTimeout(() => this.connectWebSocket(), 3000);
        };
    }

    handleMessage(message) {
        switch (message.type) {
            case 'CHAT':
                this.displayMessage(message);
                break;
            case 'JOIN':
                this.addStatusMessage(`${message.sender}님이 입장했습니다`);
                break;
            case 'LEAVE':
                this.addStatusMessage(`${message.sender}님이 퇴장했습니다`);
                break;
            case 'TYPING':
                this.showTypingIndicator(message.sender);
                break;
            default:
                console.log('Unknown message type:', message);
        }
    }

    setupEventListeners() {
        // Send message on Enter (Shift+Enter for new line)
        const messageInput = document.getElementById('messageInput');
        if (messageInput) {
            messageInput.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            });

            // Typing indicator
            messageInput.addEventListener('input', () => {
                this.handleTyping();
            });

            // Auto-resize textarea
            messageInput.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = Math.min(this.scrollHeight, 120) + 'px';
            });
        }

        // Send button
        const sendBtn = document.getElementById('sendBtn');
        if (sendBtn) {
            sendBtn.addEventListener('click', () => this.sendMessage());
        }

        // Sidebar toggle (mobile)
        const menuBtn = document.getElementById('menuBtn');
        if (menuBtn) {
            menuBtn.addEventListener('click', () => this.toggleSidebar());
        }

        // Mobile overlay
        const overlay = document.getElementById('mobileOverlay');
        if (overlay) {
            overlay.addEventListener('click', () => this.toggleSidebar());
        }

        // Tab switching
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.switchTab(e.target.dataset.tab);
            });
        });

        // Logout
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.logout());
        }

        // File upload
        const fileInput = document.getElementById('fileInput');
        if (fileInput) {
            fileInput.addEventListener('change', (e) => this.handleFileUpload(e));
        }
    }

    sendMessage() {
        const input = document.getElementById('messageInput');
        const text = input.value.trim();

        if (!text || !this.ws || this.ws.readyState !== WebSocket.OPEN) return;

        const message = {
            type: 'CHAT',
            roomId: this.currentRoom,
            sender: this.username,
            content: text,
            timestamp: new Date().toISOString()
        };

        this.ws.send(JSON.stringify(message));
        input.value = '';
        input.style.height = 'auto';

        // Stop typing indicator
        this.isTyping = false;
    }

    displayMessage(message) {
        const container = document.getElementById('messagesContainer');
        if (!container) return;

        const isSent = message.sender === this.username;

        const messageGroup = document.createElement('div');
        messageGroup.className = `message-group ${isSent ? 'sent' : 'received'}`;

        const time = new Date(message.timestamp).toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        });

        messageGroup.innerHTML = `
            <div class="message-avatar">${this.getInitial(message.sender)}</div>
            <div class="message-content">
                ${!isSent ? `<div class="message-sender">${message.sender}</div>` : ''}
                <div class="message-bubble">${this.escapeHtml(message.content)}</div>
                <div class="message-time">${time}</div>
            </div>
        `;

        // Remove typing indicator if exists
        const typingEl = container.querySelector('.typing-indicator');
        if (typingEl) {
            typingEl.remove();
        }

        container.appendChild(messageGroup);
        this.scrollToBottom();
    }

    addStatusMessage(text) {
        const container = document.getElementById('messagesContainer');
        if (!container) return;

        const statusDiv = document.createElement('div');
        statusDiv.className = 'status-message';
        statusDiv.textContent = text;
        container.appendChild(statusDiv);
        this.scrollToBottom();
    }

    showTypingIndicator(username) {
        if (username === this.username) return;

        const container = document.getElementById('messagesContainer');
        if (!container) return;

        // Remove existing typing indicator
        let typingEl = container.querySelector('.typing-indicator');

        if (!typingEl) {
            typingEl = document.createElement('div');
            typingEl.className = 'typing-indicator';
            typingEl.innerHTML = `
                <div class="message-avatar">${this.getInitial(username)}</div>
                <div class="typing-dots">
                    <div class="typing-dot"></div>
                    <div class="typing-dot"></div>
                    <div class="typing-dot"></div>
                </div>
            `;
            container.appendChild(typingEl);
            this.scrollToBottom();
        }

        // Auto-remove after 3 seconds
        clearTimeout(this.typingTimeout);
        this.typingTimeout = setTimeout(() => {
            if (typingEl && typingEl.parentNode) {
                typingEl.remove();
            }
        }, 3000);
    }

    handleTyping() {
        if (!this.isTyping) {
            this.isTyping = true;

            if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                this.ws.send(JSON.stringify({
                    type: 'TYPING',
                    roomId: this.currentRoom,
                    sender: this.username
                }));
            }
        }

        clearTimeout(this.typingTimeout);
        this.typingTimeout = setTimeout(() => {
            this.isTyping = false;
        }, 1000);
    }

    joinRoom(roomId) {
        if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;

        this.currentRoom = roomId;

        const message = {
            type: 'JOIN',
            roomId: roomId,
            sender: this.username
        };

        this.ws.send(JSON.stringify(message));

        // Clear messages
        const container = document.getElementById('messagesContainer');
        if (container) {
            container.innerHTML = '';
        }

        this.addStatusMessage(`${roomId} 방에 입장했습니다`);

        // Update header
        const headerName = document.getElementById('chatHeaderName');
        const headerStatus = document.getElementById('chatHeaderStatus');
        if (headerName) headerName.textContent = roomId;
        if (headerStatus) headerStatus.textContent = '온라인';

        // Close sidebar on mobile
        this.closeSidebar();
    }

    switchTab(tabName) {
        // Update active tab button
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === tabName);
        });

        // Load data based on tab
        switch (tabName) {
            case 'chats':
                this.loadConversations();
                break;
            case 'friends':
                this.loadFriends();
                break;
            case 'rooms':
                this.loadRooms();
                break;
        }
    }

    loadConversations() {
        const conversationList = document.getElementById('conversationList');
        if (!conversationList) return;

        // Sample data - replace with API call
        const conversations = [
            {
                id: 'general',
                name: '일반 채팅',
                avatar: '🌍',
                lastMessage: '환영합니다!',
                time: '방금',
                unread: 0,
                online: true
            },
            {
                id: 'random',
                name: '랜덤 채팅',
                avatar: '🎲',
                lastMessage: '새로운 대화를 시작해보세요',
                time: '1분 전',
                unread: 3,
                online: false
            }
        ];

        conversationList.innerHTML = conversations.map(conv => `
            <div class="conversation-item ${conv.id === this.currentRoom ? 'active' : ''}"
                 onclick="chatApp.joinRoom('${conv.id}')">
                <div class="conversation-avatar">
                    ${conv.avatar}
                    ${conv.online ? '<div class="online-indicator"></div>' : ''}
                </div>
                <div class="conversation-info">
                    <div class="conversation-name">
                        <span>${conv.name}</span>
                        <span class="conversation-time">${conv.time}</span>
                    </div>
                    <div class="conversation-preview">${conv.lastMessage}</div>
                </div>
                ${conv.unread > 0 ? `<div class="unread-badge">${conv.unread}</div>` : ''}
            </div>
        `).join('');
    }

    loadFriends() {
        const conversationList = document.getElementById('conversationList');
        if (!conversationList) return;

        conversationList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">👥</div>
                <div class="empty-state-text">아직 친구가 없습니다</div>
            </div>
        `;
    }

    loadRooms() {
        this.loadConversations(); // For now, same as conversations
    }

    toggleSidebar() {
        const sidebar = document.querySelector('.sidebar');
        const overlay = document.getElementById('mobileOverlay');

        if (sidebar) sidebar.classList.toggle('active');
        if (overlay) overlay.classList.toggle('active');
    }

    closeSidebar() {
        const sidebar = document.querySelector('.sidebar');
        const overlay = document.getElementById('mobileOverlay');

        if (sidebar) sidebar.classList.remove('active');
        if (overlay) overlay.classList.remove('active');
    }

    handleFileUpload(event) {
        const file = event.target.files[0];
        if (!file) return;

        // TODO: Implement file upload to server
        this.showNotification(`파일 업로드: ${file.name}`, 'info');
    }

    logout() {
        if (confirm('정말 로그아웃 하시겠습니까?')) {
            if (this.ws) this.ws.close();
            localStorage.clear();
            window.location.href = '/';
        }
    }

    scrollToBottom() {
        const container = document.getElementById('messagesContainer');
        if (container) {
            container.scrollTop = container.scrollHeight;
        }
    }

    getInitial(name) {
        return name ? name.charAt(0).toUpperCase() : '?';
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 90px;
            right: 20px;
            background: ${type === 'success' ? '#10B981' : type === 'error' ? '#EF4444' : '#667eea'};
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.3);
            z-index: 10000;
            animation: slideIn 0.3s ease;
            max-width: 300px;
        `;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }
}

// Animation styles
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Initialize chat app
let chatApp;
document.addEventListener('DOMContentLoaded', () => {
    chatApp = new ChatApp();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (chatApp && chatApp.ws) {
        chatApp.ws.close();
    }
});
