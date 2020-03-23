package io.miq.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import io.miq.moviecatalogservice.models.CatalogItem;
import io.miq.moviecatalogservice.models.Movie;
import io.miq.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;
	/*
	 * @Autowired private WebClient.Builder webClientBuilder;
	 */

	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

		// Get all related Movie Ids
		UserRating ratings = restTemplate.getForObject("http://movie-rating-service/ratings/users/" + userId,
				UserRating.class);

		// For each Movie Id, call MovieInfo servie to get related details
		return ratings.getUserRating().stream().map(rating -> {
			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

			// We can also make asynchronous REST call using WebClient (Reactive
			// Web)
			/*
			 * Movie movie = webClientBuilder.build().get().uri(
			 * "http://localhost:8081/movies/" + rating.getMovieId())
			 * .retrieve().bodyToMono(Movie.class).block();
			 */

			// Putting everything together
			return new CatalogItem(movie.getName(), "Tom Cruise starer block buster movie", rating.getRating());
		}).collect(Collectors.toList());
	}
}
