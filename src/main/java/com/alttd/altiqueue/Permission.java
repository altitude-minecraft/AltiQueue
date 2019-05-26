package com.alttd.altiqueue;

public enum Permission
{
    PRIORITY_QUEUE("altiqueue.priority-queue"),
    SKIP_QUEUE("altiqueue.skip=queue"),
    SILENT_PLACE("fortuneblocks.silent"),
    PICKUP("fortuneblocks.pickup"),
    ADMIN("fortuneblocks.admin"),
    ADMIN_RELOAD("fortuneblocks.admin.reload"),
    ADMIN_LIST("fortuneblocks.admin.list"),
    ADMIN_REMOVE("fortuneblocks.admin.remove"),
    ADMIN_ADD("fortuneblocks.admin.add");

    private String permission;

    private Permission(String permission)
    {
        this.permission = permission;
    }

    public String getPermission()
    {
        return permission;
    }

}
