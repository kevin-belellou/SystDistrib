#include "calculs.h"

long factoriel(int nb)
{
  int i;
  long res;
  res = 1;
  for (i=nb;i>1;i--)
    res = res * i;
  return res;
}


void analyser_donnees(int donnees[], int taille, struct res_analyse_donnees *res)
{
  int i;
  long somme;

  res -> min = donnees[0];
  res -> max = donnees[0];
  somme = donnees[0];
  
  for (i=1; i < taille; i++)
    {
      if (donnees[i] > res -> max) res -> max = donnees[i];
      if (donnees[i] < res -> min) res -> min = donnees[i];
      somme += donnees[i];
    }
  res -> moy = ((float)somme) / taille;
}

long puissance(int nb, int puiss)
{ 
  long res = nb;
  int i;
  for (i=1; i < puiss; i++)
    res = res * nb;
  return res;
}


