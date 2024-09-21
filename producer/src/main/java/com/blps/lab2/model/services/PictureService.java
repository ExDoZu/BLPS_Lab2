//package com.blps.lab2.model.services;
//
//import java.io.BufferedOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Date;
//
//import org.apache.tika.Tika;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.blps.common.UserHistoryDto;
//import com.blps.common.UserHistoryDto.UserAction;
//import com.blps.lab2.model.beans.post.User;
//import com.blps.lab2.model.repository.post.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//import java.io.File;
//
//@Service
//@RequiredArgsConstructor
//public class PictureService {
//
//    private final UserRepository userRepository;
//    private final KafkaService kafkaService;
//
//    public class GetResult {
//        private String mimeType;
//        private byte[] imageBytes;
//
//        public GetResult(String mimeType, byte[] imageBytes) {
//            this.mimeType = mimeType;
//            this.imageBytes = imageBytes;
//        }
//
//        public String getMimeType() {
//            return mimeType;
//        }
//
//        public byte[] getImageBytes() {
//            return imageBytes;
//        }
//    }
//
//    public GetResult getPicture(String name, String userPhone) throws IOException {
//        Path path = Paths.get("./uploads/" + name);
//        byte[] imageBytes = Files.readAllBytes(path);
//        File file = path.toFile();
//        Tika tika = new Tika();
//        String mimeType = tika.detect(file);
//
//        if (userPhone != null) {
//            User user = userRepository.findByPhoneNumber(userPhone);
//            if (user != null) {
//                UserHistoryDto userHistory = new UserHistoryDto(
//                        null, user.getId(), UserAction.GET_PHOTO, null, name, Date.from(java.time.Instant.now()));
//                kafkaService.send("user_audit", user.getId().toString(), userHistory);
//            }
//        }
//
//        return new GetResult(mimeType, imageBytes);
//    }
//
//    public String savePicture(MultipartFile file, String userPhone) throws IOException {
//        byte[] bytes = file.getBytes();
//        String newFileName = java.util.UUID.randomUUID().toString();
//        try (BufferedOutputStream stream = new BufferedOutputStream(
//                new FileOutputStream(new File("./uploads/" + newFileName)))) {
//            stream.write(bytes);
//        }
//
//        if (userPhone != null) {
//            User user = userRepository.findByPhoneNumber(userPhone);
//            if (user != null) {
//                UserHistoryDto userHistory = new UserHistoryDto(
//                        null, user.getId(), UserAction.ADD_PHOTO, null, newFileName, Date.from(java.time.Instant.now()));
//                kafkaService.send("user_audit", user.getId().toString(), userHistory);
//            }
//        }
//
//        return newFileName;
//    }
//}
