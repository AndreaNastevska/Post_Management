    package mk.ukim.finki.wp.jan2024g2.service.Impl;

    import mk.ukim.finki.wp.jan2024g2.model.Post;
    import mk.ukim.finki.wp.jan2024g2.model.PostType;
    import mk.ukim.finki.wp.jan2024g2.model.Tag;
    import mk.ukim.finki.wp.jan2024g2.model.exceptions.InvalidPostIdException;
    import mk.ukim.finki.wp.jan2024g2.repository.PostRepository;
    import mk.ukim.finki.wp.jan2024g2.repository.TagRepository;
    import mk.ukim.finki.wp.jan2024g2.service.PostService;
    import mk.ukim.finki.wp.jan2024g2.service.TagService;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.util.List;

    @Service
    public class PostServiceImpl implements PostService {

        private final PostRepository postRepository;
        private final TagRepository tagRepository;
        private final TagService tagService;

        public PostServiceImpl(PostRepository postRepository, TagRepository tagRepository, TagService tagService) {
            this.postRepository = postRepository;
            this.tagRepository = tagRepository;
            this.tagService = tagService;
        }

        @Override
        public List<Post> listAll() {
            return postRepository.findAll();
        }

        @Override
        public Post findById(Long id) {
            return postRepository.findById(id).orElseThrow(InvalidPostIdException::new);
        }

        @Override
        public Post create(String title, String content, LocalDate dateCreated, PostType postType, List<Long> tagsIds) {
            List <Tag> tags = tagRepository.findAllById(tagsIds);
            Post post = new Post(title, content, dateCreated, postType, tags);
            return postRepository.save(post);
        }

        @Override
        public Post update(Long id, String title, String content, LocalDate dateCreated, PostType postType, List<Long> tagsIds) {
            List <Tag> tags = tagRepository.findAllById(tagsIds);
            Post post = findById(id);

            post.setTitle(title);
            post.setContent(content);
            post.setDateCreated(dateCreated);
            post.setPostType(postType);
            post.setTags(tags);

            return postRepository.save(post);
        }

        @Override
        public Post delete(Long id) {
            Post post = findById(id);
            postRepository.delete(post);
            return post;
        }

        @Override
        public Post like(Long id) {
            Post post = findById(id);
            post.setLikes(post.getLikes() + 1);
            return postRepository.save(post);
        }

        @Override
        public List<Post> filterPosts(Long tagId, PostType postType) {
            if (tagId == null && postType == null) {
                return listAll();
            }
            else if (tagId == null) {
                return postRepository.findByPostType(postType);
            }
            else if (postType == null) {
                Tag tag = this.tagService.findById(tagId);
                return postRepository.findByTagsContains(tag);
            }
            else {
                Tag tag = this.tagService.findById(tagId);
                return postRepository.findByTagsContainsAndPostType(tag,postType);
            }
        }

        }
