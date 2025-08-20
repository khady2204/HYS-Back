package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Message;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.MessageRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepo;

    @Mock
    private NotificationService notificationService;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, userRepository, messageRepo, notificationService);
    }

    @Test
    void getGroupedDiscussions_groupsMessagesByOtherUser() {
        User currentUser = new User();
        currentUser.setId(1L);

        User currentUserClone1 = new User();
        currentUserClone1.setId(1L);

        User currentUserClone2 = new User();
        currentUserClone2.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Message m1 = new Message();
        m1.setSender(currentUserClone1);
        m1.setReceiver(otherUser);
        m1.setTimestamp(Instant.now());

        Message m2 = new Message();
        m2.setSender(otherUser);
        m2.setReceiver(currentUserClone2);
        m2.setTimestamp(Instant.now().plusSeconds(1));

        when(messageRepository.findBySenderIdOrReceiverId(anyLong(), anyLong()))
                .thenReturn(List.of(m1, m2));

        Map<User, List<Message>> grouped = messageService.getGroupedDiscussions(currentUser);

        assertEquals(1, grouped.size());
        User key = grouped.keySet().iterator().next();
        assertEquals(2L, key.getId());
        assertEquals(2, grouped.get(key).size());
    }

    @Test
    void markMessageAsRead_setsReadFlag() {
        User receiver = new User();
        receiver.setId(1L);
        User sender = new User();
        sender.setId(2L);

        Message message = new Message();
        message.setId(10L);
        message.setSender(sender);
        message.setReceiver(receiver);

        when(messageRepository.findById(10L)).thenReturn(Optional.of(message));

        messageService.markMessageAsRead(10L, receiver);

        assertTrue(message.isRead());
        verify(messageRepository).save(message);
    }
}
