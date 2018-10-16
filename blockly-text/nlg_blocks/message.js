Blockly.Blocks['message'] = {
    init: function() {
        this.jsonInit({
            "message0": 'describe %1',
            "args0": [
                {
                    "type": "input_value",
                    "name": "VALUE",
                    "check": "String"
                }
            ],
            "output": "String",
            "colour": 160,
            "tooltip": "Returns a text describing product attribute.",
            "helpUrl": "http://www.todo-augmentedcopywriter.com/help"
        });
    }
};
