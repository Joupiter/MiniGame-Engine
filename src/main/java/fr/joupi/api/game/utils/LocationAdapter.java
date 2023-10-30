package fr.joupi.api.game.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

	@Override
	public Location deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		final JsonObject object = (JsonObject) json;

		return new Location(Bukkit.getWorld(object.get("world").getAsString()), object.get("x").getAsDouble(), object.get("y").getAsDouble(), object.get("z").getAsDouble(), object.get("yaw").getAsFloat(), object.get("pitch").getAsFloat());
	}

	@Override
	public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject obj = new JsonObject();

		obj.addProperty("world", location.getWorld().getName());
		obj.addProperty("x", location.getX());
		obj.addProperty("y", location.getY());
		obj.addProperty("z", location.getZ());
		obj.addProperty("yaw", location.getYaw());
		obj.addProperty("pitch", location.getPitch());

		return obj;
	}

}