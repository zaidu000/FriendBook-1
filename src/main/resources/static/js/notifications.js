// Accept Friend Request
function handleAccept(button) {
	const username = button.getAttribute("data-username"); // sender's username

	fetch(`/friend-request/accept/${username}`, {
		method: 'POST',
		credentials: 'include'
	})
		.then(res => res.json())
		.then(data => {
			if (data.showFollowBack) {
				const reqDiv = document.getElementById(`request-${username}`);
				reqDiv.innerHTML = `
                <span>You are now connected with ${data.senderUsername}</span>
                <button class="follow-back-btn" 
                        data-username="${data.senderUsername}" 
                        onclick="handleFollowBack(this)">Follow Back</button>
            `;
			} else {
				document.getElementById(`request-${username}`).remove();
			}
		})
		.catch(err => console.error("Error accepting request:", err));
}

// Follow Back
function handleFollowBack(button) {
	const username = button.getAttribute("data-username"); // target username

	fetch(`/friend-request/follow-back/${username}`, {  // ✅ use follow-back endpoint
		method: 'POST',
		credentials: 'include'
	})
		.then(res => res.json())
		.then(data => {
			if (data.message) {
				button.innerText = "Followed Back";
				button.disabled = true;
			} else {
				alert("Failed to send follow back request");
			}
		})
		.catch(err => console.error("Error sending follow back:", err));
}


// Decline Friend Request
function handleDecline(button) {
	const username = button.getAttribute("data-username");

	fetch(`/friend-request/decline/${username}`, {
		method: 'POST',
		credentials: 'include'
	})
		.then(res => {
			if (res.ok) {
				document.getElementById(`request-${username}`).remove();
			} else {
				alert("Failed to decline request");
			}
		})
		.catch(err => console.error("Error declining request:", err));
}
