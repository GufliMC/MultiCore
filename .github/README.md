# MultiCore

An extension to [MultiLib](https://github.com/MultiPaper/MultiLib) which add type-safe packet communication between servers and
automatically syncs type-safe data.

## Platforms
Note: Java 17 is required

* [x] MultiPaper
* [x] Bukkit / Spigot / Paper (will use no-operations)

## API

### Maven
```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    implementation 'com.guflimc.multicore:multipaper:+'
}
```

### Usage

Check the [javadocs](https://guflimc.github.io/MultiCore/)

#### Examples

An instance of MultiCore will only communicate with instances of multicore on other servers that are created by the same plugin.

```java
// initialize
@Override
public void onEnable(){
    MultiPaperCore multicore = MultiPaperCore.of(this);
}

// create a packet (all fields must be serializable
public class CoolPacket extends Packet {
    public String message;
}

// send packet
CoolPacket packet = new CoolPacket();
packet.message = "Hello World!";
multicore.send(packet);

// subscribe to packets
multicore.subscribe(CoolPacket.class, packet -> {
    Bukkit.broadcastMessage(packet.message);
});

// sync data across servers
AttributeKey<Integer> serverLevel = new AttributeKey("SERVER_LEVEL", Integer.class);
multicore.storage().setAttribute(attribute, 69);

// get attribute
int level = multicore.storage().getAttribute(serverLevel).orElse(1);

// subscribe to changes
multicore.storage().subscribe(serverLevel, level -> {
    Bukkit.broadcastMessage("Server level changed to " + level);
});

// you can also sync data that belongs to players
Player player = Bukkit.getPlayer("iGufGuf");
multicore.storage(player).setAttribute(serverLevel, 420);
```

