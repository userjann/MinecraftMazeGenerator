package ch.mc.jr.labyrinth;

import org.bukkit.Location;

import java.util.Objects;

public class XZCords {
    private int x;
    private int z;

    public XZCords(int x, int z) {
        this.x = x;
        this.z = z;
    }
    public XZCords(Location location){
        this.x = location.getBlockX();
        this.z = location.getBlockZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XZCords){
           XZCords other = (XZCords) obj;
           if(other.x == this.x && other.z == this.z){
               return true;
           }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
