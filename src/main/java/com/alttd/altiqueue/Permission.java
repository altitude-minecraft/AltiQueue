package com.alttd.altiqueue;

public enum Permission
{
    PRIORITY_QUEUE("altiqueue.priority-queue"),
    SKIP_QUEUE("altiqueue.skip-queue"),
    QUEUE_COMMAND("altiqueue.queue-command");

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
