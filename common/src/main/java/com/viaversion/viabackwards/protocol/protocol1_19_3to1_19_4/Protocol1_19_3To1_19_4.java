/*
 * This file is part of ViaVersion - https://github.com/ViaVersion/ViaVersion
 * Copyright (C) 2023 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.viabackwards.protocol.protocol1_19_3to1_19_4;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.protocol.protocol1_19_3to1_19_4.packets.EntityPackets1_19_4;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_19_3Types;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ServerboundPackets1_19_4;
import com.viaversion.viaversion.rewriter.CommandRewriter;

public final class Protocol1_19_3To1_19_4 extends BackwardsProtocol<ClientboundPackets1_19_4, ClientboundPackets1_19_3, ServerboundPackets1_19_4, ServerboundPackets1_19_3> {

    private final EntityPackets1_19_4 entityRewriter = new EntityPackets1_19_4(this);

    public Protocol1_19_3To1_19_4() {
        super(ClientboundPackets1_19_4.class, ClientboundPackets1_19_3.class, ServerboundPackets1_19_4.class, ServerboundPackets1_19_3.class);
    }

    @Override
    protected void registerPackets() {
        // TODO fallback field in components
        executeAsyncAfterLoaded(Protocol1_19_3To1_19_1.class, () -> {
        });

        entityRewriter.register();

        final CommandRewriter<ClientboundPackets1_19_4> commandRewriter = new CommandRewriter<ClientboundPackets1_19_4>(this) {
            @Override
            public void handleArgument(final PacketWrapper wrapper, final String argumentType) throws Exception {
                if (argumentType.equals("minecraft:time")) {
                    wrapper.read(Type.INT); // Minimum
                } else {
                    super.handleArgument(wrapper, argumentType);
                }
            }

            @Override
            protected String argumentType(final int argumentTypeId) {
                return Protocol1_19_3To1_19_1.MAPPINGS.getArgumentTypeMappings().mappedIdentifier(argumentTypeId);
            }

            @Override
            protected int mappedArgumentTypeId(final String mappedArgumentType) {
                return Protocol1_19_3To1_19_1.MAPPINGS.getArgumentTypeMappings().mappedId(mappedArgumentType);
            }
        };
        commandRewriter.registerDeclareCommands1_19(ClientboundPackets1_19_4.DECLARE_COMMANDS);

        cancelClientbound(ClientboundPackets1_19_4.BUNDLE);
    }

    @Override
    public void init(final UserConnection user) {
        addEntityTracker(user, new EntityTrackerBase(user, Entity1_19_3Types.PLAYER));
    }
}