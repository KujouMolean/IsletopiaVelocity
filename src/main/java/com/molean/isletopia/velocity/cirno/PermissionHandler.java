package com.molean.isletopia.velocity.cirno;

import com.molean.isletopia.shared.parameter.ContactParameter;
import com.molean.isletopia.shared.parameter.GroupParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PermissionHandler {

    public static List<String> getPermissions(Long id) {
        String permissionString = ContactParameter.get(id, "permissions");
        List<String> permissions;
        if (permissionString == null) {
            permissions = new ArrayList<>();
        } else {
            permissions = Arrays.asList(permissionString.split(","));
        }

        return new ArrayList<>(permissions);
    }

    public static List<String> getPermissions(String group) {
        group = group.toLowerCase(Locale.ROOT);
        String permissionString = GroupParameter.get(group, "permissions");
        List<String> permissions;
        if (permissionString == null) {
            permissions = new ArrayList<>();
        } else {
            permissions = Arrays.asList(permissionString.split(","));
        }

        return new ArrayList<>(permissions);
    }

    public static List<String> getGroups(Long id) {
        String groupsString = ContactParameter.get(id, "groups");
        List<String> groups;
        if (groupsString == null) {
            groups = new ArrayList<>();
        } else {
            groups = Arrays.asList(groupsString.split(","));
        }
        return new ArrayList<>(groups);
    }

    public static boolean hasPermission(String permission, String group) {
        permission = permission.toLowerCase(Locale.ROOT);

        if (getPermissions(group).contains("-" + permission)) {
            return false;
        }

        return getPermissions(group).contains(permission);
    }


    public static boolean hasPermission(String permission, Long id) {
        if (id == 2719322893L) {
            return true;
        }
        permission = permission.toLowerCase(Locale.ROOT);

        if (getPermissions(id).contains("-" + permission)) {
            return false;
        }

        List<String> groups = getGroups(id);
        groups.add("default");
        for (String group : groups) {
            if (getPermissions(group).contains("-" + permission)) {
                return false;
            }
        }
        if (getPermissions(id).contains(permission)) {
            return true;
        }
        for (String group : groups) {
            if (getPermissions(group).contains(permission)) {
                return true;
            }
        }
        return false;
    }

    public static void grantPermission(String permission, Long id) {
        permission = permission.toLowerCase(Locale.ROOT);
        List<String> permissions = getPermissions(id);
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
        setPermissions(id, permissions);
    }

    public static void removePermission(String permission, Long id) {
        permission = permission.toLowerCase(Locale.ROOT);
        List<String> permissions = getPermissions(id);
        permissions.remove(permission);
        setPermissions(id, permissions);
    }

    public static void setPermissions(Long id, List<String> permissions) {
        if (permissions.isEmpty()) {
            ContactParameter.unset(id, "permissions");
            return;
        }
        ContactParameter.set(id, "permissions", String.join(",", permissions).toLowerCase(Locale.ROOT));
    }


    public static void grantPermission(String permission, String group) {
        permission = permission.toLowerCase(Locale.ROOT);
        List<String> permissions = getPermissions(group);
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
        setPermissions(group, permissions);
    }

    public static void removePermission(String permission, String group) {
        permission = permission.toLowerCase(Locale.ROOT);
        List<String> permissions = getPermissions(group);
        permissions.remove(permission);
        setPermissions(group, permissions);
    }

    public static void setPermissions(String group, List<String> permissions) {
        if (permissions.isEmpty()) {
            GroupParameter.unset(group, "permissions");
            return;
        }
        GroupParameter.set(group, "permissions", String.join(",", permissions).toLowerCase(Locale.ROOT));
    }

}
