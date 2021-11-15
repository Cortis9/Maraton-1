package com.bda.gameLogic;

public interface IDBRelationable {
    void SaveToDB();
    void GetFromDB(Object ...params);
}
