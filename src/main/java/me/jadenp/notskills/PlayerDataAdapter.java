package me.jadenp.notskills;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

public class PlayerDataAdapter extends TypeAdapter<PlayerData> {

    @Override
    public void write(JsonWriter jsonWriter, PlayerData playerData) throws IOException {
        jsonWriter.beginObject();
        // check for null value
        if (playerData == null) {
            jsonWriter.nullValue();
            return;
        }
        // format skills in one string
        String skl = "";
        StringBuilder skills = new StringBuilder();
        for (String s : playerData.getSkillsUnlocked()){
            skills.append(s).append(",");
        }
        if (skills.length() > 0)
            skl = skills.substring(0, skills.length()-1);
        // write data
        jsonWriter.name("uuid");
        jsonWriter.value(playerData.getUuid().toString());
        jsonWriter.name("skills");
        jsonWriter.value(skl);
        jsonWriter.endObject();
    }

    @Override
    public PlayerData read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        // check for null value
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        // read uuid
        String groupName = null;
        PlayerData playerData = new PlayerData();
        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();

            if (token.equals(JsonToken.NAME)) {
                //get the current token
                groupName = jsonReader.nextName();
            }

            if ("uuid".equals(groupName)) {
                //move to next token
                jsonReader.peek();
                playerData.setUuid(UUID.fromString(jsonReader.nextString()));
            }

            if("skills".equals(groupName)) {
                //move to next token
                jsonReader.peek();
                String skills = jsonReader.nextString();
                String[] splitSkills = skills.split(",");
                playerData.setSkills(splitSkills);
            }
        }
        jsonReader.endObject();
        return playerData;
    }
}
