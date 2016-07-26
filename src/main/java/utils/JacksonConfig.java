package utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Jackson config is a utility class to read / write XML and JSON configuration file.
 * 
 * Created by taihuynh on 18/7/16.
 */
public class JacksonConfig {
    private String fileName;
    private Object configBean;
    private Class<?> beanClass;

    private boolean ignoreUnknownProperty;

    private static Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    private JacksonConfigFormat configFormat;
    private ObjectMapper mapper;

    /**
     * This method is a convenient method to read a configuration file.
     * It will first detect if the filename is defined in a system variable
     *
     * If no configuration variable is defined, it will use the default filename
     *
     * After obtaining the filename, it will try to locate the file. First it will see if the filename
     * is an absolute path, if it is not, then it will try to locate the filename in the classpath.
     *
     * @param defaultFileName the default file name of the file we are looking for.
     * @param systemVar the name of the system variable whose value will be the file name of the file
     * we are looking for. This has higher priority than {@code defaultFileName}
     * @param resultClass this is the class for the type of the resultant object
     * @param format this is the format of the file. By default, it will be {@code JacksonConfigFormat.XML}
     *
     * @return an instance of the class {@code <T>} with fields defined in the configuration file.
     */
    public static <T> T readConfig(String defaultFileName, String systemVar, Class<T> resultClass, JacksonConfigFormat format) {
        T result = null;

        String fileName = null;

        if (systemVar != null) fileName = System.getProperty(systemVar);;

        if (fileName == null) fileName = defaultFileName;

        File f = new File(fileName);

        if (!f.exists()) {
            // search in the classpath

            logger.trace("Searching {} in class path!", fileName);

            URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);

            if (url == null) {
                logger.info("Cannot find configuration file {} in class path!", fileName);
                return result;
            }

            try {

                fileName = url.toURI().getPath();

            } catch (URISyntaxException e) {

                throw new RuntimeException("The uri syntax of the configuration filename is not correct!", e);

            }
        }

        JacksonConfig jc = new JacksonConfig(fileName, resultClass, format);
        result = resultClass.cast(jc.getConfigBean());

        return result;
    }

    /**
     * Initialize a configuration reader with an input file and the expected return of a certain type
     * @param file the file to read the configuration from
     * @param c type of the object to hold result
     */
    public JacksonConfig(String file, Class<?> c) {
        this(file, c, JacksonConfigFormat.XML);
    }

    /**
     * Initialize a configuration reader with an input file of a certain format (JSON or XML)
     * and the expected return of a certain type.
     * @param file the file to read the configuration from
     * @param c type of the object to hold result
     * @param format format of the file
     */
    public JacksonConfig(String file, Class<?> c, JacksonConfigFormat format) {
        setFileName(file);
        setConfigFormat(format);

        if (file != null && c != null)
            configBean = readConfig(fileName, c);
    }

    /**
     * Initialize a Jackson config with a file format
     * @param format the format of the file to read or write later
     */
    public JacksonConfig(JacksonConfigFormat format) {
        this(null, null, format);
    }

    /**
     * Initialize a default JacksonConfig. XML format will be used to read and write
     *
     */
    public JacksonConfig() {
        setConfigFormat(JacksonConfigFormat.XML);
    }

    /**
     * Read a configuration file with specified name and store the result
     * into an object of the type specified
     * @param fileName name of the file to be read
     * @param c type of the result object
     * @return the object holding the configuration. Null is returned if there is exception or cannot be parsed or mapped.
     */
    public Object readConfig(String fileName, Class<?> c) {
        if (this.isIgnoreUnknownProperty())
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(new File(fileName), c);
        } catch (JsonParseException e) {
            throw new RuntimeException("Cannot parse the configuration file.", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Mapping exception while parsing configuration file", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurs. Does the file exist?", e);
        }
    }

    /**
     * Read configuration from a String and store into object of the specified type
     * @param configString the string of the configuration (in the format (XML / JSON) of this JacksonConfig)
     * @param c the type of the result object
     * @return the object holding the configuration. Null is returned if cannot be parsed or mapped.
     */
    public Object readConfigString(String configString, Class<?> c) {
        if (this.isIgnoreUnknownProperty())
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(configString, c);
        } catch (JsonParseException e) {
            throw new RuntimeException("Cannot parse the configuration string.", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Mapping exception while parsing configuration string.", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurs.", e);
        }
    }

    /**
     * Write the configuration into the file (which should be specified in the constructor or set earlier)
     *
     * @param prettyPrint whether or not to use pretty printing
     */
    public void saveConfig(boolean prettyPrint) {
        try {
            File destFile = new File(fileName);
            if (destFile.getParentFile() != null && !destFile.exists()) {
                destFile.getParentFile().mkdirs();
            }
            if (!prettyPrint) mapper.writeValue(destFile, configBean);
            else {
                JsonGenerator jgen = mapper.getFactory().createGenerator(new PrintWriter(destFile));
                PrettyPrinter pp = getPrettyPrinter();
                jgen.setPrettyPrinter(pp);
                mapper.writeValue(jgen, configBean);
            }
        } catch (IOException e) {
            logger.error("Exception while writing config file", e);
        }
    }

    private PrettyPrinter getPrettyPrinter() {
        switch (configFormat) {
            case XML:
                return new DefaultXmlPrettyPrinter();
            case JSON:
                return new DefaultPrettyPrinter();
        }
        return null;
    }

    /**
     * Save the configuration in an object to a file of a specified name
     * @param fileName the file to be saved into
     * @param value the configuration object
     * @param prettyPrint whether or not to use pretty print
     */
    public void saveConfig(String fileName, Object value, boolean prettyPrint) {
        setFileName(fileName);
        setConfigBean(value);

        saveConfig(prettyPrint);
    }

    /**
     * Get the current file name
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the name of the file to be used in save and read config
     * @param fileName name of the file
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Whether or not we should ignore unknown properties when reading a configuration.
     *
     * If set to false and when unknown properties are encountered in the configuration file, exception will be thrown
     *
     * @return true if enabled, false otherwise
     */
    public boolean isIgnoreUnknownProperty() {
        return ignoreUnknownProperty;
    }

    /**
     * Set whether or not we should ignore unknown properties when reading a configuration
     * @param ignoreUnknownProperty true or false
     */
    public void setIgnoreUnknownProperty(boolean ignoreUnknownProperty) {
        this.ignoreUnknownProperty = ignoreUnknownProperty;
    }

    /**
     * If we initialized the jackson config instance with a configuration file and the type of the result object,
     * the file will be read automatically and the result can be retrieved using this method.
     *
     * @return The object holding the configuration
     */
    public Object getConfigBean() {
        return configBean;
    }

    /**
     * If we call this function, and call the saveConfig this config bean will be used as the source of values
     * to be saved
     * @param obj The object holding the configuration values
     */
    public void setConfigBean(Object obj) {
        configBean = obj;
        setBeanClass(obj.getClass());
    }

    /**
     * Type of the config bean
     * @return the type class of the config bean
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * Set the config bean type class
     * @param beanClass
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * Get the format to be used by this JacksonConfig
     * @return the config format, either XML or JSON
     */
    public JacksonConfigFormat getConfigFormat() {
        return configFormat;
    }

    /**
     * Set the configuration format to be used. Either XML or JSON
     * @param configFormat
     */
    public void setConfigFormat(JacksonConfigFormat configFormat) {
        this.configFormat = configFormat;
        switch (this.configFormat) {
            case XML:
                mapper = new XmlMapper();
                return;
            case JSON:
                mapper = new ObjectMapper();
                return;
        }
    }

    public void registerModule(Module module) {
        mapper.registerModule(module);
    }
}

