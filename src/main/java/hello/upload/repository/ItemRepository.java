package hello.upload.repository;


import hello.upload.domain.Item;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepository {
    private static final ConcurrentHashMap<Long, Item> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0L);

    public Item save(Item item) {
        item.setId(sequence.incrementAndGet());
        store.put(sequence.get(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void clear() {
        store.clear();
        sequence.set(0L);
    }
}
