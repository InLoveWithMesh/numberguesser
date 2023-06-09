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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;


// - Other Dependencies - //
import java.util.Map;
import java.util.HashMap;

@Controller
@SpringBootApplication
public class NumberGuesser2Application {
	//@Autowired
	public gameDictionary sessionDict = new gameDictionary();


	public Object getResponse(game myGame, int guessedNumber)
	{
		int randomNumber = myGame.getRandomNumber();
		int upperborder = myGame.getUpperborder();
		int lowerborder = myGame.getLowerborder();
		boolean hit = true;
		String requestlog = myGame.getRequestlog();
		String body = "Congratulations! Your Number: "+guessedNumber+" is exactly the same as the generated number!";

		if(randomNumber < guessedNumber)
		{

			if(upperborder > guessedNumber){myGame.setUpperborder(guessedNumber);upperborder=guessedNumber;}
			body = "Your Number: "+guessedNumber+" is larger than the generated number!\nThe number is between "+lowerborder+" and "+upperborder+"\n("+lowerborder+" < i < "+upperborder+")!";
			hit = false;
		}
		else if(randomNumber > guessedNumber)
		{

			if(lowerborder < guessedNumber) {myGame.setLowerborder(guessedNumber);lowerborder=guessedNumber;}
			body = "Your Number: "+guessedNumber+" is smaller than the generated number!\nThe number is between "+lowerborder+" and "+upperborder+"\n("+lowerborder+" < i < "+upperborder+")!";
			hit = false;
		}

		Map<String, Object> object = new HashMap<>();
		object.put("status", "200");
		object.put("message", body);
		object.put("lowerBorder", lowerborder);
		object.put("upperBorder", upperborder);
		object.put("guessedNumber", guessedNumber);
		object.put("requestLog", requestlog);
		object.put("user", myGame.getGameOwner().getUsername());
		object.put("numberOfTry", myGame.getRequestnumber());
		object.put("hit", hit);
		return object;
	}

	public static void main(String[] args) {
		SpringApplication.run(NumberGuesser2Application.class, args);
	}

	@GetMapping(value="/start-game", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object startGame(Authentication authentication) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User gameOwner = (User) userDetails;

		System.out.println("Neues Game für: " + gameOwner.getUsername()+" wurde erfolgreich erstellt!");
		if (sessionDict.containsGame(gameOwner.getUsername())){sessionDict.removeGame(gameOwner.getUsername());}
		game myGame = new game(gameOwner);

		sessionDict.addGame(gameOwner.getUsername(),myGame);

		Map<String, Object> object = new HashMap<>();
		object.put("status", "200");
		object.put("user", gameOwner.getUsername());
		object.put("message", "Successfully started a new Game!");
		return object;
	}

	@GetMapping(value="/guess", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object guesser(Authentication authentication,@RequestParam(name = "number") int guessedNumber)
	{
		game myGame;
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User gameOwner = (User) userDetails;

		if (!sessionDict.containsGame(gameOwner.getUsername())){startGame(authentication);}
		myGame = sessionDict.getGame(gameOwner.getUsername());
		System.out.println("New User: '"+gameOwner.getUsername().toUpperCase()+"' Guessed: "+guessedNumber+" Real Number: "+myGame.getRandomNumber());

		myGame.updateRequestnumber();
		myGame.updateRequestlog( myGame.getRequestnumber()+". Guessed Number: "+guessedNumber+"\n");
		return getResponse(myGame, guessedNumber);
	}

	@GetMapping("/logout")
	public String logout() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {SecurityContextHolder.getContext().setAuthentication(null);}
		return "redirect:/login?logout";
	}

}
