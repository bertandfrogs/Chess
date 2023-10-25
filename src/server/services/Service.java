package server.services;

/**
 * The Service base class provides the basic structure for all Services in general.
 * It's an abstract class because it can't be instantiated.
 * Service class gets called to perform a requested function, getting passed a Java request object
 */
public abstract class Service {
    /**
     * Method for serializing a Java object. May prove useful, or I may need some other way to go back and forth between Java and JSON.
     * @return The JSON string of an Object
     */
    public String javaObjectToJSON(){
        return null;
    }

    /**
     * Method for deserializing a Java object. May prove useful, or I may need some other way to go back and forth between Java and JSON.
     * @param JSON A JSON string
     * @return The Object from a JSON string.
     */
    public Object JSONtoJavaObject(String JSON){
        return null;
    }
}
