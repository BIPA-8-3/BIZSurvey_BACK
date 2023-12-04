package com.bipa.bizsurvey.domain.community.application;

import com.bipa.bizsurvey.domain.community.domain.PostImages;
import com.bipa.bizsurvey.domain.community.domain.QPostImages;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostImageResponse;
import com.bipa.bizsurvey.domain.community.repository.PostImagesRepository;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostImageService {

    private final PostRepository postRepository;
    private final PostImagesRepository postImagesRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QPostImages pi = QPostImages.postImages;
    public void createPostImages(Long postId, List<String> createUrlList){

        List<PostImages> postImages = new ArrayList<>();

        for (String s : createUrlList) {
            if(!checkAlreadyExist(s)){
                PostImages image = PostImages.builder()
                        .imgName(s)
                        .post(postRepository.findById(postId).get())
                        .build();
                postImages.add(image);
            }
        }
         postImagesRepository.saveAll(postImages);
    }

    public List<PostImageResponse> getImageList(Long postId){
        List<PostImages> postImages = jpaQueryFactory
                .select(pi)
                .from(pi)
                .where(pi.post.id.eq(postId))
                .where(pi.delFlag.eq(false))
                .fetch();
        List<PostImageResponse> imageResponseList = new ArrayList<>();
        for (PostImages image : postImages) {
            PostImageResponse postImageResponse = PostImageResponse.builder()
                    .postImageId(image.getId())
                    .postImageUrl(image.getImgName())
                    .build();
            imageResponseList.add(postImageResponse);
        }
        return imageResponseList;
    }

    public void deletePostImages(Long postId, List<String> deleteUrlList){
        for(String s : deleteUrlList){
            if(checkAlreadyExist(s)){

                List<PostImages> postImages = jpaQueryFactory
                        .select(pi)
                        .from(pi)
                        .where(pi.post.id.eq(postId))
                        .where(pi.imgName.eq(s))
                        .where(pi.delFlag.eq(false))
                        .fetch();


                for (PostImages postImage : postImages) {
                    postImage.setDelFlag(true);
                    postImagesRepository.save(postImage);
                }
            }
        }
    }




    public boolean checkAlreadyExist(String imageUrl){
        return postImagesRepository.existsByImgName(imageUrl);
    }



}
