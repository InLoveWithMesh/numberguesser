package com.example.numberguesser2;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;


// - Other Dependencies - //
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@SpringBootApplication
public class NumberGuesser2Application {
	private final personRepository personRepository;
	private final gameRepository gameRepository;

	@Autowired
	public NumberGuesser2Application(personRepository personRepository, com.example.numberguesser2.gameRepository gameRepository) {
		this.personRepository = personRepository;
		this.gameRepository = gameRepository;
	}


	public static void main(String[] args) {
		SpringApplication.run(NumberGuesser2Application.class, args);
	}

	public HashMap<String, Object> getResponse(game myGame, int guessedNumber)
	{
		int randomNumber = myGame.getRandomNumber();
		int upperborder = myGame.getUpperborder();
		int lowerborder = myGame.getLowerborder();
		String body;

		if((randomNumber < guessedNumber))
		{

			if(upperborder > guessedNumber){
				myGame.setUpperborder(guessedNumber);
			}
			body = "Your Number: "+guessedNumber+" is larger than the generated number!\nThe number is between "+myGame.getLowerborder()+" and "+myGame.getUpperborder()+"\n("+myGame.getLowerborder()+" < i < "+myGame.getUpperborder()+")!";
		}
		else if(randomNumber > guessedNumber)
		{

			if(lowerborder < guessedNumber) {
				myGame.setLowerborder(guessedNumber);
			}
			body = "Your Number: "+guessedNumber+" is smaller than the generated number!\nThe number is between "+myGame.getLowerborder()+" and "+myGame.getUpperborder()+"\n("+myGame.getLowerborder()+" < i < "+myGame.getUpperborder()+")!";
		}
		else {
			myGame.setHit(true);
			body = "Congratulations! Your Number: "+guessedNumber+" is exactly the same as the generated number!";
		}
		HashMap<String, Object> object = new HashMap<>();
		object.put("status", "200");
		object.put("message", body);
		object.put("lowerBorder", myGame.getLowerborder());
		object.put("upperBorder", myGame.getUpperborder());
		object.put("guessedNumber", guessedNumber);
		object.put("requestLog", myGame.getRequestlog2());
		object.put("user", myGame.getGameOwner().getName());
		object.put("numberOfTry", myGame.getRequestnumber());
		object.put("hit", myGame.isHit());
		gameRepository.save(myGame);
		return object;
	}

	@GetMapping(value="/guess", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object guess(Authentication authentication,@RequestParam(name = "number") int guessedNumber)
	{
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User gameOwner = (User) userDetails;
		person myUser = getPerson(authentication);

		if (!myUser.hasRunningGame())
		{
			startGame(authentication);

		}

		game myGame = myUser.getCurrentGame();
		myGame.updateRequestlog(guessedNumber);

		System.out.println("New User: '"+gameOwner.getUsername().toUpperCase()+"' Guessed: "+guessedNumber+" Real Number: "+myGame.getRandomNumber());
		Object response = getResponse(myGame, guessedNumber);
		//getResponse(myGame, guessedNumber);
		//gameRepository.save(myGame);
		return response;
	}

	@GetMapping(value = "/getgames", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object getGames(@RequestParam(name = "name") String pName) {
		Optional<person> foundPerson = personRepository.findByName(pName);
		if (foundPerson.isPresent()) {
			return foundPerson.get().getGames();
		} else {
			Map<String, Object> object = new HashMap<>();
			object.put("Status", 404);
			return object;
		}
	}

	@GetMapping(value="/start-game", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object startGame(Authentication authentication) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User gameOwner = (User) userDetails;
		String username = gameOwner.getUsername();
		System.out.println("Neues Game für: " + gameOwner.getUsername()+" wurde erfolgreich erstellt!");

		person myUser = getPerson(authentication);
		//if (sessionDict.containsGame(username)) {
		//	sessionDict.removeGame(gameOwner.getUsername());
		//}
		//sessionDict.addGame(username,myGame);

		game myGame = new game(myUser);
		myUser.addGame(myGame);
		gameRepository.save(myGame);

		Map<String, Object> object = new HashMap<>();
		object.put("status", "200");
		object.put("user", gameOwner.getUsername());
		object.put("message", "Successfully started a new Game!");
		return object;
	}

	@GetMapping(value="/getperson", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public person getPerson(Authentication authentication) {
		Optional<person> existingPerson = personRepository.findByName(getUsername(authentication));
		if (existingPerson.isEmpty()) {
			person newPerson = new person(getUsername(authentication));
			personRepository.save(newPerson);
			return newPerson;
		} else {
			return existingPerson.get();
		}
	}

	@GetMapping(value="/detailedlog", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object detailedlog(Authentication authentication) {
		Map<String, Object> object = new HashMap<>();

		object.put("user", getUsername(authentication));


		Optional<person> existingPerson = personRepository.findByName(getUsername(authentication));
		if (existingPerson.isEmpty()) {
			person newPerson = new person(getUsername(authentication));
			personRepository.save(newPerson);
			object.put("message", "None");
		} else
		{
			person myUser = existingPerson.get();
			StringBuilder response = new StringBuilder();
			for(int g = 0; g<myUser.getGames().size(); g++){
				if (myUser.getGames().get(g).isHit()){
					response.append("Game ").append(Integer.toString(g+1)).append(": ").append(Integer.toString(myUser.getGames().get(g).getRequestnumber())).append("x Guesses to Hit Number: ").append(Integer.toString(myUser.getGames().get(g).getRandomNumber())).append("\n");
				}
				else{
					response.append("Game ").append(Integer.toString(g+1)).append(": ").append(Integer.toString(myUser.getGames().get(g).getRequestnumber())).append("x Guesses to Not Hit Number: ").append(Integer.toString(myUser.getGames().get(g).getRandomNumber())).append("\n");
				}
			}
			object.put("message", response.toString());

		}
		return object;
	}

	@GetMapping(value="/get-username")
	@ResponseBody
	public String getUsername(Authentication authentication) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User gameOwner = (User) userDetails;

		return gameOwner.getUsername();
	}

	@GetMapping("/logout")
	public String logout() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {SecurityContextHolder.getContext().setAuthentication(null);}
		return "redirect:/login?logout";
	}

}
