package com.nathanaelg.jasmine;

import com.nathanaelg.jasmine.date.TimeStamp;
import com.nathanaelg.jasmine.messages.*;
import com.nathanaelg.jasmine.user.crud.api.UserCrudService;
import com.nathanaelg.jasmine.user.user.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.CachedRowSet;
import javax.validation.Valid;
import java.util.List;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@SpringBootApplication(scanBasePackages = {"com.nathanaelg.jasmine"})
@RestController
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MessagesForJasmineApplication {

    @NonNull
    UserCrudService users;

    public static void main(String[] args) {
        SpringApplication.run(MessagesForJasmineApplication.class, args);
    }

    @GetMapping(path = "/get-message")
    public Message returnMessage(@AuthenticationPrincipal final User user) throws Exception {
        return Database.getMessage(user.getUsername());
    }

    @PostMapping(path = "/get-message")
    public Message returnMessage(@AuthenticationPrincipal final User user, @RequestParam("message-id") final int messageID) throws Exception {
        Message message = Database.getMessage(messageID);
        if (user.getUsername().equals(message.getSender())) {
            return message;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only message sender or receiver may view this message");
    }

    @PostMapping(path = "/set-message")
    public ResponseEntity setMessage(@RequestBody @Valid final MessageUpdateBean messageUpdateBean, @AuthenticationPrincipal final User sender) throws Exception {
        ResponseEntity responseEntity;
        String recipientUserName = messageUpdateBean.getRecipient();
        if (users.findUsername(recipientUserName)) {
            String senderUserName = sender.getUsername();
            if (!recipientUserName.equals(senderUserName)) {
                Database.setMessage(new Message(-1, recipientUserName, senderUserName, messageUpdateBean.getTitle(), messageUpdateBean.getMessage(), TimeStamp.getCurrentTimeStamp(), messageUpdateBean.getPriority()));
                responseEntity = new ResponseEntity<>(new MessageResponse(TimeStamp.getCurrentTimeStamp(), HttpStatus.ACCEPTED, "Your message has been accepted."), HttpStatus.ACCEPTED);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipient cannot be the same as the sender.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find recipient.");
        }
        return responseEntity;
    }

    @PostMapping(path = "/change-priority")
    public ResponseEntity changePriority(@AuthenticationPrincipal final User user, @RequestParam("message-id") int id, @RequestParam("priority") int priority) throws Exception {
        Message message = Database.getMessage(id);
        if (user.getUsername().equals(message.getSender())) {
            Database.executeUpdate("UPDATE " + Database.Tables.MESSAGES + " SET priority = ? WHERE id = ?;", priority, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Priority may only be changed by the original sender");
        }
    }

    @GetMapping("/get-sent-messages")
    public List<SummarizedMessage> returnAllUserMessages(@AuthenticationPrincipal final User user) throws Exception {
        List<SummarizedMessage> messages = Database.getAllSentMessages(user.getUsername());
        if (messages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "There are no messages");
        }
        return messages;
    }

    @DeleteMapping("/delete-message")
    public ResponseEntity deleteMessage(@AuthenticationPrincipal final User user, @RequestParam("message-id") final int messageID) throws Exception {
        Message message = Database.getMessage(messageID);
        if (user.getUsername().equals(message.getSender())) {
            Database.archiveMessage(messageID, true);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only message sender may delete this message");
        }
    }

    @PostMapping("/edit-message")
    public ResponseEntity editMessage(@AuthenticationPrincipal final User user, @RequestParam("message-id") final int messageID, @RequestBody @Valid final MessageUpdateBean messageUpdate) throws Exception {
        if (user.getUsername().equals(Database.getMessageSender(messageID))) {
            if (Database.checkExists(Database.Tables.MESSAGES, "id = ? AND msgRead = ?", messageID, true)) {
                throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Message recipient has already read this message.");
            }
            Message newMessage = Database.getMessage(messageID);
            newMessage.setTitle(messageUpdate.getTitle());
            newMessage.setMessage(messageUpdate.getMessage());
            newMessage.setPriority(messageUpdate.getPriority());
            newMessage.setTimestamp(TimeStamp.getCurrentTimeStamp());
            Database.updateMessage(newMessage);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only message sender may edit this message");
        }
    }

    @PostMapping("/view-message-status")
    public MessageStatusBean retrieveMessageStatus(@AuthenticationPrincipal final User user, @RequestParam("message-id") final int messageID) throws Exception {
        Message message = Database.getMessage(messageID);
        if (user.getUsername().equals(message.getSender())) {
            CachedRowSet resultSet = Database.executeQuery("SELECT title, recipient, msgRead, msgReadTs FROM " + Database.Tables.MESSAGES + " WHERE id = ?;", messageID);
            resultSet.next();
            return new MessageStatusBean(resultSet.getString("title"),
                    resultSet.getString("recipient"),
                    resultSet.getBoolean("msgRead"),
                    resultSet.getString("msgReadTs"));
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only message sender may view message status");
        }
    }
}