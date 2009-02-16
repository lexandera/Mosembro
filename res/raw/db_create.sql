
CREATE TABLE actions (
    action_id TEXT PRIMARY KEY, 
    type TEXT,
    handles TEXT,
    name TEXT, 
    description TEXT, 
    script TEXT,
    icon BLOB,
    enabled INTEGER
);
