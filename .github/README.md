# MultiCore

An extension to [MultiLib](https://github.com/MultiPaper/MultiLib) which add packet communication between servers and
automatically syncs global and player data. MultiCore allows you to send any serializable object to another server and hides all the complex stuff to make this happen.
All the serialization is done automatically so you can just keep on developing with simple java objects. More advanced patterns like shared memory and request/response are also available and very easy to use.

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

#### Tutorial

An instance of MultiCore will only communicate with instances of multicore on other servers that are created by the same plugin. This is because each plugin has its own communication channel.

Initialize in the onEnable for your plugin.
```java
public MultiPaperCore multicore;

@Override
public void onEnable(){
    multicore = MultiPaperCore.of(this);
}
```

Normal packets
```java
// create a packet type (all fields must be serializable)
public class CoolPacket extends Packet {
    public String message;
}

// create packet
CoolPacket packet = new CoolPacket();
packet.message = "Hello World!";

// send packet
multicore.send(packet);

// subscribe to packets
multicore.subscribe(CoolPacket.class, packet -> {
    Bukkit.broadcastMessage(packet.message);
});
```

Callback packets
```java
// create a request packet type (all fields must be serializable)
public class CoolRequestPacket extends RequestPacket<CoolPacket> {
    public String request;
    public CoolRequestPacket() {
        super(CoolPacket.class);
    }
}

// subscribe for requests
multicore.subscribe(CoolRequestPacket.class, (packet, respond) -> {
    // handle request
    Bukkit.broadcastMessage(packet.message);
    
    // create response
    CoolPacket response = new CoolPacket();
    response.message = "World!";

    // send response
    respond.accept(response);
});

// create request
CoolRequestPacket request = new CoolRequestPacket();
request.request = "Hello";

// send request
multicore.request(request).thenAccept(response -> {
    // handle response
    Bukkit.broadcastMessage(response.message);
});
```

Sync data across servers, this is file-storage persistent and not recommended for large objects.
You can store any serializable object with a nice developer experience.
```java
// create an attribute key
public final static AttributeKey<Integer> SCORE = new AttributeKey("SCORE", Integer.class);

// set global attribute value 
multicore.storage().setAttribute(SCORE, 69);

// get global attribute value
int score = multicore.storage().attribute(SCORE).orElse(0);
Bukkit.broadcastMessage("Server score is " + score);

// subscribe to changes
multicore.storage().subscribe(SCORE, (score) -> {
    Bukkit.broadcastMessage("Server score changed to " + score);
});

// you can also sync player data
Player player = Bukkit.getPlayer("iGufGuf");
multicore.storage(player).setAttribute(SCORE, 420);
```
