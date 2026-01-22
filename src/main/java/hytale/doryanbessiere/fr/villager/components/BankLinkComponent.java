package hytale.doryanbessiere.fr.villager.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class BankLinkComponent implements Component<EntityStore> {

    public static final BuilderCodec<BankLinkComponent> CODEC;

    private static ComponentType<EntityStore, BankLinkComponent> componentType;

    public static void setComponentType(ComponentType<EntityStore, BankLinkComponent> componentType) {
        BankLinkComponent.componentType = componentType;
    }

    public static ComponentType<EntityStore, BankLinkComponent> getComponentType() {
        return componentType;
    }

    static {
        CODEC = BuilderCodec.builder(BankLinkComponent.class, BankLinkComponent::new)
                .append(new KeyedCodec<UUID>("BankUuid", Codec.UUID_STRING),
                        (c, v) -> c.bankUuid = v, c -> c.bankUuid)
                .add().build();
    }

    private UUID bankUuid;

    public BankLinkComponent() {
    }

    public BankLinkComponent(UUID bankUuid) {
        this.bankUuid = bankUuid;
    }

    public UUID getBankUuid() {
        return bankUuid;
    }

    public void setBankUuid(UUID bankUuid) {
        this.bankUuid = bankUuid;
    }

    @Override
    public @Nullable Component clone() {
        return new BankLinkComponent(this.bankUuid);
    }

    @Override
    public @Nullable Component cloneSerializable() {
        return Component.super.cloneSerializable();
    }
}