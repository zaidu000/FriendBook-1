async function submitComment(event, postId) {
	event.preventDefault();
	const form = event.target;
	const input = form.querySelector('input[name="text"]');
	const text = input.value;

	if (!text.trim()) return;

	const response = await fetch(`/comments/${postId}`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({ text })
	});

	if (response.ok) {
		const username = document.querySelector("meta[name='username']").getAttribute("content"); // You can also pass it from server

		// Append new comment directly to the DOM
		const commentsDiv = document.getElementById(`comments-${postId}`);
		const newComment = document.createElement('p');
		newComment.innerHTML = `<b>${username}</b>: ${text}`;
		commentsDiv.appendChild(newComment);

		input.value = '';
	} else {
		alert("Failed to post comment");
	}
}

async function loadComments(postId) {
	const res = await fetch(`/api/comments/${postId}`);
	if (res.ok) {
		const comments = await res.json();
		const commentDiv = document.getElementById(`comments-${postId}`);
		commentDiv.innerHTML = "";
		comments.forEach(c => {
			const p = document.createElement("p");
			p.innerHTML = `<b>${c.user.username}</b>: ${c.text}`;
			commentDiv.appendChild(p);
		});
	}
}
async function toggleLike(postId) {
	const res = await fetch(`/api/likes/${postId}`, { method: 'POST' });
	if (res.ok) {
		const newCount = await res.text();
		document.getElementById(`like-count-${postId}`).innerText = newCount;
	} else {
		alert("Failed to like/unlike");
	}
}

function deleteComment(commentId) {
	if (!confirm("Are you sure you want to delete this comment?")) return;

	fetch(`/comments/delete/${commentId}`, {
		method: 'DELETE'
	})
	.then(res => {
		if (res.ok) {
			alert("Comment deleted");
			location.reload();
		} else {
			res.text().then(msg => alert("Error: " + msg));
		}
	})
	.catch(err => {
		console.error("Error deleting comment:", err);
		alert("Something went wrong");
	});
}