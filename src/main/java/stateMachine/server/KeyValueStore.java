package stateMachine.server;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import stateMachine.commands.Get;
import stateMachine.commands.Put;
import stateMachine.commands.Delete;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeyValueStore extends StateMachine {
    private Map<Object, Commit> storage = new HashMap<>();

    public Object put(Commit<Put> commit) {
        Commit<Put> put = storage.put(commit.operation().key, commit);
        return put == null ? null : put.operation().value;
    }

    public void get(Commit<Get> commit) {
        /*
        try {
            Commit<Put> put = map.get(commit.operation().key);
            return put == null ? null : put.operation().value;
        } finally {
            commit.release();
        }
        */
         return;
    }

    public Object delete(Commit<Delete> commit) {
        Commit<Put> put = null;
        try {
            put = storage.remove(commit.operation().key);
            return put == null ? null : put.operation().value;
        } finally {
            if (put != null)
                put.release();
            commit.release();
        }
    }

    public static void main( String[] args ){
        int myId = Integer.parseInt(args[0]);
        List<Address> addresses = new LinkedList<>();

        for(int i = 1; i <args.length; i+=2)
        {
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
            addresses.add(address);
        }

        CopycatServer.Builder builder = CopycatServer.builder(addresses.get(myId));

        builder.withStateMachine(KeyValueStore::new);
        builder.withTransport(NettyTransport.builder()
                .withThreads(4)
                .build());
        builder.withStorage(Storage.builder()
                .withDirectory(new File("logs_" +myId))
                .withStorageLevel(StorageLevel.DISK)
                .build());

        System.out.println(myId);

        CopycatServer server = builder.build();

        // O primeiro cria o Cluster
        if(myId == 0)
        {
            server.bootstrap().join();
        }
        // Os outros vao entra no primeiro Cluster
        else
        {
            server.join(addresses).join();
        }

    }
}

