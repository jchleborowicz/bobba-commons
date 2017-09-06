package org.bobba.tools.statest.common.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.apache.commons.lang3.Validate.notEmpty;

public class FileBasedTestStateRepository implements TestStateRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedTestStateRepository.class);

    private final File baseDirectory;

    public FileBasedTestStateRepository(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        if (baseDirectory.exists()) {
            if (!baseDirectory.isDirectory()) {
                throw new RuntimeException(baseDirectory + " is not a directory.");
            }
        } else {
            if (!baseDirectory.mkdir()) {
                throw new RuntimeException("Could not create directory " + baseDirectory);
            }
        }
        LOGGER.info("Test state repository initialized in directory " + baseDirectory);
    }

    @Override
    public synchronized <T> T load(String objectId, Class<T> aClass) {
        notEmpty(objectId);
        final File source = new File(baseDirectory, objectId);

        if (!source.exists()) {
            throw new TestStateObjectDoesNotExistException("Object with id " + objectId + " not found in repository");
        }

        if (!source.isFile()) {
            throw new RuntimeException("File expected: " + source);
        }

        return loadFromFile(source, aClass);
    }

    private <T> T loadFromFile(File source, Class<T> aClass) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(source));
            final Object result = in.readObject();
            if (!aClass.isInstance(result)) {
                throw new RuntimeException("Object read from " + source + " is not of expected type. Expected type: "
                        + aClass.getName() + ", actual type: " + result.getClass().getName());
            }
            //noinspection unchecked
            return (T) result;
        } catch (IOException e) {
            throw new RuntimeException("Exception when reading file " + source, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Exception when reading file " + source, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("Error when closing file " + source, e);
                }
            }
        }
    }

    @Override
    public synchronized void store(String objectId, Object object) {
        notEmpty(objectId);
        final File file = new File(baseDirectory, objectId);
        if (file.exists()) {
            if (!file.isFile()) {
                throw new RuntimeException(file + " is not a file");
            }
            if (!file.delete()) {
                throw new RuntimeException("Could not remove file " + file);
            }
        }

        if (object != null) {
            ObjectOutputStream outputStream = null;
            try {
                outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(object);
                LOGGER.info("Stored test state object. Id: " + objectId + ", value: " + object);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Error when writing value to file " + file, e);
            } catch (IOException e) {
                throw new RuntimeException("Error when writing value to file " + file, e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Could not close file " + file);
                    }
                }
            }
        }
    }

}
