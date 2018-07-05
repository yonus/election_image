package com.kula.controller.upload;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class PictureUploadController {

    @PostMapping("/picture")
    public String uploadPicture(@RequestParam("picture") MultipartFile uploadfile , @RequestParam("extraField") String extraField){
             if(uploadfile.isEmpty()){
                 return "";
             }

            ImageAnnotatorClient vision = null;
             try{
                 byte[] fileContent = uploadfile.getBytes();
                 vision = ImageAnnotatorClient.create();
                 ByteString imgBytes = ByteString.copyFrom(fileContent);
                 List<AnnotateImageRequest>  requests = new ArrayList<>();
                 Image img = Image.newBuilder().setContent(imgBytes).build();
                 Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
                 AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                         .addFeatures(feat)
                         .setImage(img)
                         .build();
                 requests.add(request);

                 BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
                 List<AnnotateImageResponse> responses = response.getResponsesList();

                 for (AnnotateImageResponse res : responses) {
                     if (res.hasError()) {
                         System.out.printf("Error: %s\n", res.getError().getMessage());
                         return "";
                     }

                     for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                         annotation.getAllFields().forEach((k, v) ->
                                 System.out.printf("%s : %s\n", k, v.toString()));
                     }
                 }

             }catch (Exception e){
                e.printStackTrace();
             }


            System.out.print("yunus");
            return new String("yunus");
    }



}
