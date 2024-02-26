package com.ej.hgj.entity.cst;

public class QrCode {
    private String expire_seconds;
    private String action_name;
    private ActionInfo action_info;

    public QrCode(String expire_seconds, String action_name, String scene_id, String scene_str) {
        this.expire_seconds = expire_seconds;
        this.action_name = action_name;
        ActionInfo actionInfo = new ActionInfo();
        if (action_name.contains("STR")) {
            SceneStr sceneStr = new SceneStr();
            sceneStr.setScene_str(scene_str);
            actionInfo.setScene(sceneStr);
        } else {
            SceneId sceneId = new SceneId();
            sceneId.setScene_id(scene_id);
            actionInfo.setScene(sceneId);
        }

        this.action_info = actionInfo;
    }

    public String getExpire_seconds() {
        return this.expire_seconds;
    }

    public void setExpire_seconds(String expire_seconds) {
        this.expire_seconds = expire_seconds;
    }

    public String getAction_name() {
        return this.action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public ActionInfo getAction_info() {
        return this.action_info;
    }

    public void setAction_info(ActionInfo action_info) {
        this.action_info = action_info;
    }
}
