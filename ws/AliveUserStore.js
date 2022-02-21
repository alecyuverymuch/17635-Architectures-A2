class AliveUserStore {
    constructor(){
        this._aliveUsers = new Set();
    }

    addAliveUser(user) {
        this._aliveUsers.add(user);
    }

    removeAliveUser(user) {
        this._aliveUsers.delete(user);
    }

    isUserAlive(user) {
        return this._aliveUsers.has(user);
    }
}

const aliveUserStore = new AliveUserStore();
Object.freeze(aliveUserStore);
module.exports = aliveUserStore;
