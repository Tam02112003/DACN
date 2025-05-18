function toggleChat() {
    const chatContainer = document.getElementById('chatContainer');
    chatContainer.style.display = chatContainer.style.display === 'none' ? 'block' : 'none';
}

function sendMessage() {
    const input = document.getElementById('fanfootball-user-input');
    const message = input.value.trim();

    if (!message) return;

    addMessage('user', message);
    input.value = '';

    // Hiển thị trạng thái loading
    const typingId = "typing-" + Date.now();
    addMessage('bot', '<div id="' + typingId + '"><i>Đang xử lý...</i></div>');

    fetch('/api/ai/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({ message: message })
    })
    .then(async response => {
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Lỗi server');
        }
        return response.json();
    })
    .then(data => {
        document.getElementById(typingId).remove();
        addMessage('bot', data.response);
    })
    .catch(error => {
        document.getElementById(typingId).remove();
        addMessage('bot', 'Lỗi: ' + error.message);
        console.error('Error:', error);
    });
}

function addMessage(sender, text) {
    const messagesDiv = document.getElementById('chatMessages');
    const messageElement = document.createElement('div');
    messageElement.innerHTML = `
        <div style="margin-bottom: 10px; text-align: ${sender === 'user' ? 'right' : 'left'}">
            <span style="display: inline-block; padding: 8px 12px;
                background: ${sender === 'user' ? '#e3f2fd' : '#f1f1f1'};
                border-radius: 10px; max-width: 80%; word-wrap: break-word">
                ${text}
            </span>
        </div>
    `;
    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;

    // Kích hoạt lại các sự kiện sau khi thêm HTML mới
    activateProductLinks();
}

// Hàm để kích hoạt sự kiện click cho các liên kết sản phẩm
function activateProductLinks() {
    document.querySelectorAll('.product-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const productName = this.getAttribute('data-product');
            document.getElementById('fanfootball-user-input').value = productName;
            sendMessage();
        });
    });
}

// Xử lý phím Enter
document.getElementById('fanfootball-user-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') sendMessage();
});