package co.edu.uptc.gamemanagement.entities.keyEmbedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerKey implements Serializable {

    @Column()
    private int idGame;
    @Column()
    private int idPiece;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayerKey that = (GamePlayerKey) o;
        return Objects.equals(idGame, that.idGame) &&
                Objects.equals(idPiece, that.idPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGame, idPiece);
    }
}
