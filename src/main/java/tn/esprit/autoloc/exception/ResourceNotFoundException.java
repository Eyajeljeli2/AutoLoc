package tn.esprit.autoloc.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable avec l'identifiant " + id);
    }

    public ResourceNotFoundException(String ressource, String identifier) {
        super(ressource + " introuvable avec : " + identifier);
    }
}
