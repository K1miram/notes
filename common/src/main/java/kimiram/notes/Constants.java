package kimiram.notes;

import kimiram.imagelib.ImageLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "notes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ImageLib imageHelper = new ImageLib(MOD_ID, new ImageLib.Size(128, 128));
}
