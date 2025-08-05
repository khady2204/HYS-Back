package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageRequest;
import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageResponse;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Message;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.MessageRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepo;

    public MessageResponse sendMessage(User sender, MessageRequest request) {
        User senderEntity = userRepository.findById(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(senderEntity);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setTimestamp(Instant.now());

        if (request.getMediaFile() != null && !request.getMediaFile().isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads";
                Files.createDirectories(Paths.get(uploadDir));

                String original = Objects.requireNonNull(request.getMediaFile().getOriginalFilename());
                String extension = "";
                int dot = original.lastIndexOf('.');
                if (dot >= 0) {
                    extension = original.substring(dot);
                }

                String fileName = UUID.randomUUID() + extension;
                Path filePath = Paths.get(uploadDir, fileName);
                request.getMediaFile().transferTo(filePath.toFile());

                String mediaUrl = "/media/" + fileName;
                message.setMediaUrl(mediaUrl);
                message.setMediaType(request.getMediaType());
            } catch (IOException e) {
                throw new RuntimeException("Could not store media file", e);
            }
        }

        Message savedMessage = messageRepository.save(message);

        return new MessageResponse(
                savedMessage.getId(),
                savedMessage.getSender().getId(),
                savedMessage.getReceiver().getId(),
                savedMessage.getContent(),
                savedMessage.getTimestamp(),
                savedMessage.getMediaUrl(),
                savedMessage.getMediaType()
        );
    }

    public List<MessageResponse> getMessagesBetweenUsers(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User1 not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User2 not found"));

        List<Message> messages = messageRepository.findBySenderAndReceiverOrderByTimestampAsc(user1, user2);
        messages.addAll(messageRepository.findBySenderAndReceiverOrderByTimestampAsc(user2, user1));

        return messages.stream()
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getSender().getId(),
                        m.getReceiver().getId(),
                        m.getContent(),
                        m.getTimestamp(),
                        m.getMediaUrl(),
                        m.getMediaType()
                ))
                .collect(Collectors.toList());
    }

    public Map<User, List<Message>> getGroupedDiscussions(User currentUser) {
        List<Message> all = messageRepo.findBySenderOrReceiver(currentUser, currentUser);

        Map<User, List<Message>> grouped = new HashMap<>();

        for (Message m : all) {
            // Les entités User peuvent être différentes instances représentant le même utilisateur.
            // Comparer directement les objets peut donc échouer. On compare les identifiants
            // pour déterminer si le message a été envoyé par l'utilisateur courant.
            User ami = Objects.equals(m.getSender().getId(), currentUser.getId())
                    ? m.getReceiver()
                    : m.getSender();
            grouped.computeIfAbsent(ami, k -> new ArrayList<>()).add(m);
        }

        // Trier les messages de chaque conversation
        for (List<Message> messages : grouped.values()) {
            messages.sort(Comparator.comparing(Message::getTimestamp)); // du plus ancien au plus récent
        }

        //  Retourner un Map trié par date du dernier message (du plus récent au plus ancien)
        return grouped.entrySet().stream()
                .sorted((e1, e2) -> {
                    Instant last1 = e1.getValue().get(e1.getValue().size() - 1).getTimestamp();
                    Instant last2 = e2.getValue().get(e2.getValue().size() - 1).getTimestamp();
                    return last2.compareTo(last1);
                })
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        Map::putAll);
    }


}
