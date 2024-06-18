package com.blps.lab2.repo.main;

import com.blps.lab2.model.mainDB.User;
import com.blps.lab2.utils.mapper.UserWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlUserMarshaller {

    private final String xmlFilePath = ".users.xml";
    private final File file = new File(xmlFilePath);

    public List<User> readUsers()  {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            JAXBContext context = JAXBContext.newInstance(UserWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            UserWrapper wrapper = (UserWrapper) unmarshaller.unmarshal(file);
            return wrapper.getUsers();
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to read users from XML", e);
        }
    }

    public void writeUsers(List<User> users) {
        try {
            JAXBContext context = JAXBContext.newInstance(UserWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            UserWrapper wrapper = new UserWrapper();
            wrapper.setUsers(users);
            marshaller.marshal(wrapper, file);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to write users to XML", e);
        }
    }
}
