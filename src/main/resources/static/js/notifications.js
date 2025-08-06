function handleAccept(button) {
	const username = button.getAttribute("data-username");

	fetch(`/friend-request/accept/${username}`, {
		method: 'POST',
		credentials: 'include' // Include session
	})
		.then(res => {
			if (res.ok) {
				document.getElementById(`request-${username}`).remove();
			} else {
				alert("Failed to accept request");
			}
		})
		.catch(err => console.error(err));
}

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
		.catch(err => console.error(err));
}