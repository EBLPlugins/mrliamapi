package net.mrliam2614.mrliamapi.bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RotativeLocation extends LightLocation implements Cloneable{
    private float pitch;
    private float yaw;

    public RotativeLocation(Location location){
        super(location);
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    @Override
    public Location toLocation(){
        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ(), yaw, pitch);
    }
    @Override
    public String toString() {
        return "|Mondo: " + this.getWorld() + " | x: " + this.getX() + " | y: " + this.getY() + " | z: " + this.getZ() + " | pitch: " + pitch + " | yaw: " + yaw + "|";
    }

    @Override
    public List<String> toArrayString(){
        List<String> arrayString = new ArrayList<>();
        arrayString.add("&7Mondo: &e" + getWorld());
        arrayString.add("&7x: &e" + getX());
        arrayString.add("&7y: &e" + getY());
        arrayString.add("&7z: &e" + getZ());
        arrayString.add("&7pitch: &e" + pitch);
        arrayString.add("&7yaw: &e" + yaw);
        return arrayString;
    }
}
