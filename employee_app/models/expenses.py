class Expense:
    def __init__(self, user_id:int, amount:int, description:str, date:str, id=None) -> None:
        self.id = id
        self.user_id = user_id
        self.amount = amount
        self.description = description
        self.date = date

    def __str__(self) -> str:
        return (
            f"Expense #{self.id}: ${self.amount} for {self.description} "
            f"on {self.date}"
        )
    
