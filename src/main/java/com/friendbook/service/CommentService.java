package com.friendbook.service;

import java.util.List;
import com.friendbook.model.Comment;
import com.friendbook.model.User;

public interface CommentService {

	List<Comment> getCommentsForPost(Long postId);

	void saveComment(Long postId, User user, String text);

	boolean deleteComment(Long commentId, User user);
}
