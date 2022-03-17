package com.molean.isletopia.velocity.cirno;

import com.molean.isletopia.velocity.cirno.command.ban.*;
import com.molean.isletopia.velocity.cirno.command.func.ElytraCommand;
import com.molean.isletopia.velocity.cirno.command.group.*;
import com.molean.isletopia.velocity.cirno.command.info.*;
import com.molean.isletopia.velocity.cirno.command.manage.*;
import com.molean.isletopia.velocity.cirno.command.owner.*;
import com.molean.isletopia.velocity.cirno.command.permission.GrantCommand;
import com.molean.isletopia.velocity.cirno.command.permission.PermissionCommand;
import com.molean.isletopia.velocity.cirno.command.permission.RevokeCommand;

public class CommandsRegister {
    public CommandsRegister() {
        new BanCommand();
        new PardonCommand();
        new BanIpCommand();
        new PardonIpCommand();
        new GrantCommand();
        new RevokeCommand();
        new PermissionCommand();
        new HostNameCommand();
        new ListCommand();
        new ElytraCommand();
        new PlayTimeCommand();
        new TBanCommand();
        new ParameterCommand();
        new IsBanCommand();
        new StatusCommand();
        new KillCommand();
        new AddMember();
        new GGrant();
        new GPermission();
        new GRevoke();
        new Members();
        new RemoveMember();
        new IsOnlineCommand();
        new SetTitle();
        new GrantOP();
        new RevokeOP();
        new Kick();
        new Sudo();
        new MSPT();
        new Rank();
        new Mute();
        new Unmute();
        new Nick();
        new UUIDCommand();
        new Balance();
        new Broadcast();
        new WBan();
        new WPardon();
        new WBanList();
    }
}
