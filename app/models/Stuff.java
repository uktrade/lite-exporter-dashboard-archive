package models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.PathBindable;

public class Stuff implements PathBindable<Stuff> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Stuff.class);

  public String id;

  @Override
  public Stuff bind(String key, String txt) {
    LOGGER.error(key + " " + txt);
    Stuff stuff = new Stuff();
    stuff.id = txt;
    return stuff;
  }

  @Override
  public String unbind(String key) {
    return String.valueOf(id);
  }

  @Override
  public String javascriptUnbind() {
    return null;
  }

}
