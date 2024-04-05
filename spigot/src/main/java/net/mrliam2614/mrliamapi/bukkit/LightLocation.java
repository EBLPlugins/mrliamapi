package net.mrliam2614.mrliamapi.bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LightLocation implements Cloneable {
    private String world = "world";
    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;

    public LightLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }


    @Override
    public String toString() {
        return "|Mondo: " + world + " | x: " + x + " | y: " + y + " | z: " + z + "|";
    }

    public List<String> toArrayString(){
        List<String> arrayString = new ArrayList<>();
        arrayString.add("&7Mondo: &e" + world);
        arrayString.add("&7x: &e" + x);
        arrayString.add("&7y: &e" + y);
        arrayString.add("&7z: &e" + z);
        return arrayString;
    }
}
